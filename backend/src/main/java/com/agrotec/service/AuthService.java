package com.agrotec.service;

import com.agrotec.domain.Canal;
import com.agrotec.domain.Usuario;
import com.agrotec.dto.AuthDto;
import com.agrotec.dto.UsuarioResponse;
import com.agrotec.exception.BusinessException;
import com.agrotec.exception.ConflictException;
import com.agrotec.exception.UnauthorizedException;
import com.agrotec.integration.notification.Destinatario;
import com.agrotec.integration.notification.NotificationChannelFactory;
import com.agrotec.repository.UsuarioRepository;
import com.agrotec.security.JwtService;
import com.agrotec.util.Celulares;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Registro, inicio de sesión y recuperación de contraseña (RF-01). */
@Service
public class AuthService {

    private static final int MAX_INTENTOS = 5;
    private static final Duration BLOQUEO = Duration.ofMinutes(15);
    private static final Duration VIGENCIA_CODIGO = Duration.ofMinutes(10);
    private static final String MENSAJE_RECUPERACION =
            "Si el celular está registrado, recibirás un código por SMS (válido por 10 minutos).";

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final NotificationChannelFactory canales;
    private final boolean exponerCodigo;
    private final SecureRandom random = new SecureRandom();

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt,
                       NotificationChannelFactory canales,
                       @Value("${agrotec.dev-expose-reset-code:false}") boolean exponerCodigo) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
        this.canales = canales;
        this.exponerCodigo = exponerCodigo;
    }

    public AuthDto.AuthResponse registrar(AuthDto.RegistroRequest r) {
        if (!r.aceptaDatos()) {
            throw new BusinessException("Debes aceptar el tratamiento de datos personales para crear tu cuenta");
        }
        String celular = Celulares.normalizar(r.celular());
        if (usuarios.existsByCelular(celular)) {
            throw new ConflictException("Ya existe una cuenta con este celular");
        }
        Usuario u = new Usuario();
        u.setNombre(r.nombre().trim());
        u.setCelular(celular);
        u.setPasswordHash(encoder.encode(r.password()));
        u.setConsentimientoDatos(true);
        u.setConsentimientoFecha(Instant.now());
        usuarios.save(u);
        return respuesta(u);
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest r) {
        String celular = Celulares.normalizar(r.celular());
        Usuario u = usuarios.findByCelular(celular)
                .orElseThrow(() -> new UnauthorizedException("Celular o contraseña incorrectos"));
        Instant ahora = Instant.now();
        if (u.getBloqueadoHasta() != null && u.getBloqueadoHasta().isAfter(ahora)) {
            throw new UnauthorizedException("Cuenta bloqueada por intentos fallidos. Intenta de nuevo en unos minutos.");
        }
        if (!encoder.matches(r.password(), u.getPasswordHash())) {
            u.setIntentosFallidos(u.getIntentosFallidos() + 1);
            if (u.getIntentosFallidos() >= MAX_INTENTOS) {
                u.setBloqueadoHasta(ahora.plus(BLOQUEO));
                u.setIntentosFallidos(0);
            }
            usuarios.save(u);
            throw new UnauthorizedException("Celular o contraseña incorrectos");
        }
        u.setIntentosFallidos(0);
        u.setBloqueadoHasta(null);
        usuarios.save(u);
        return respuesta(u);
    }

    /** Genera un código de 6 dígitos y lo envía por SMS. No revela si el celular existe. */
    public AuthDto.RecuperarResponse solicitarRecuperacion(AuthDto.RecuperarRequest r) {
        String celular = Celulares.normalizar(r.celular());
        String codigoDev = null;
        var encontrado = usuarios.findByCelular(celular);
        if (encontrado.isPresent()) {
            Usuario u = encontrado.get();
            String codigo = String.format("%06d", random.nextInt(1_000_000));
            u.setCodigoRecuperacionHash(encoder.encode(codigo));
            u.setCodigoRecuperacionExpira(Instant.now().plus(VIGENCIA_CODIGO));
            usuarios.save(u);
            canales.obtener(Canal.SMS).enviar(new Destinatario(u.getId(), u.getNombre(), u.getCelular()),
                    "Código de recuperación AgroTec", "Tu código es " + codigo + ". Vence en 10 minutos.");
            if (exponerCodigo) {
                codigoDev = codigo;
            }
        }
        return new AuthDto.RecuperarResponse(MENSAJE_RECUPERACION, codigoDev);
    }

    public void restablecer(AuthDto.RestablecerRequest r) {
        String celular = Celulares.normalizar(r.celular());
        Usuario u = usuarios.findByCelular(celular)
                .orElseThrow(() -> new BusinessException("Código inválido o vencido"));
        boolean valido = u.getCodigoRecuperacionHash() != null
                && u.getCodigoRecuperacionExpira() != null
                && u.getCodigoRecuperacionExpira().isAfter(Instant.now())
                && encoder.matches(r.codigo(), u.getCodigoRecuperacionHash());
        if (!valido) {
            throw new BusinessException("Código inválido o vencido");
        }
        u.setPasswordHash(encoder.encode(r.nuevaPassword()));
        u.setCodigoRecuperacionHash(null);
        u.setCodigoRecuperacionExpira(null);
        u.setIntentosFallidos(0);
        u.setBloqueadoHasta(null);
        usuarios.save(u);
    }

    private AuthDto.AuthResponse respuesta(Usuario u) {
        return new AuthDto.AuthResponse(jwt.generar(u), UsuarioResponse.de(u));
    }
}
