package com.agrotec.security;

import com.agrotec.domain.Rol;
import com.agrotec.domain.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Emite y valida tokens JWT (HS256). RNF-05. */
@Service
public class JwtService {

    private final SecretKey clave;
    private final long minutosExpiracion;

    public JwtService(@Value("${agrotec.jwt.secret}") String secreto,
                      @Value("${agrotec.jwt.expiration-minutes}") long minutosExpiracion) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("agrotec.jwt.secret debe tener al menos 32 caracteres");
        }
        this.clave = Keys.hmacShaKeyFor(bytes);
        this.minutosExpiracion = minutosExpiracion;
    }

    public String generar(Usuario u) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(u.getId())
                .claim("celular", u.getCelular())
                .claim("rol", u.getRol().name())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusSeconds(minutosExpiracion * 60)))
                .signWith(clave)
                .compact();
    }

    /** Devuelve la identidad si el token es válido y no ha expirado; vacío en caso contrario. */
    public Optional<AuthUser> validar(String token) {
        try {
            Claims c = Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
            return Optional.of(new AuthUser(c.getSubject(), c.get("celular", String.class),
                    Rol.valueOf(c.get("rol", String.class))));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
