package com.agrotec.service;

import com.agrotec.domain.Alerta;
import com.agrotec.domain.Canal;
import com.agrotec.domain.PlanTipo;
import com.agrotec.domain.PreferenciasNotificacion;
import com.agrotec.domain.TipoAlerta;
import com.agrotec.domain.Usuario;
import com.agrotec.dto.PreferenciasDto;
import com.agrotec.integration.notification.BandejaSimulada;
import com.agrotec.integration.notification.Destinatario;
import com.agrotec.integration.notification.MensajeEnviado;
import com.agrotec.integration.notification.NotificationChannelFactory;
import com.agrotec.repository.UsuarioRepository;
import com.agrotec.util.Celulares;
import com.agrotec.util.Textos;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Preferencias y envío de notificaciones por push, WhatsApp o SMS (RF-11). */
@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    private final UsuarioRepository usuarios;
    private final NotificationChannelFactory fabrica;
    private final BandejaSimulada bandeja;
    private final PlanGuard planGuard;

    public NotificacionService(UsuarioRepository usuarios, NotificationChannelFactory fabrica,
                               BandejaSimulada bandeja, PlanGuard planGuard) {
        this.usuarios = usuarios;
        this.fabrica = fabrica;
        this.bandeja = bandeja;
        this.planGuard = planGuard;
    }

    public PreferenciasDto.Response preferencias(Usuario u) {
        return PreferenciasDto.Response.de(u.getPreferencias());
    }

    public PreferenciasDto.Response actualizar(Usuario u, PreferenciasDto.Request r) {
        if (r.canales().contains(Canal.WHATSAPP) || r.canales().contains(Canal.SMS)) {
            planGuard.requierePlus(u, "Alertas por WhatsApp y SMS");
        }
        PreferenciasNotificacion p = u.getPreferencias();
        p.setCanales(new LinkedHashSet<>(r.canales()));
        p.setTipos(new LinkedHashSet<>(r.tipos()));
        p.setNumeroWhatsapp(r.numeroWhatsapp() == null || r.numeroWhatsapp().isBlank()
                ? null : Celulares.normalizar(r.numeroWhatsapp()));
        p.setSilencioInicio(blankANull(r.silencioInicio()));
        p.setSilencioFin(blankANull(r.silencioFin()));
        return PreferenciasDto.Response.de(usuarios.save(u).getPreferencias());
    }

    /** Envía una alerta por los canales elegidos, respetando tipos de alerta y horario silencioso. */
    public void notificarAlerta(Usuario u, Alerta a) {
        String cuerpo = a.getMensaje() + (a.getRecomendacion() != null ? " " + a.getRecomendacion() : "");
        enviarMensaje(u, a.getTipo(), a.getTitulo(), cuerpo);
    }

    /**
     * @param tipo tipo de alerta para filtrar según preferencias; null = mensaje transaccional
     *             (confirmación de pago, respuesta del asesor) que no depende de los tipos elegidos.
     */
    public void enviarMensaje(Usuario u, TipoAlerta tipo, String titulo, String mensaje) {
        PreferenciasNotificacion p = u.getPreferencias();
        if (tipo != null && !p.permite(tipo)) {
            return;
        }
        LocalTime ahora = LocalTime.now(Textos.ZONA);
        if (p.estaEnSilencio(ahora)) {
            log.info("Horario silencioso: no se envía '{}' a {}", titulo, u.getNombre());
            return;
        }
        String celular = p.getNumeroWhatsapp() != null ? p.getNumeroWhatsapp() : u.getCelular();
        Destinatario destino = new Destinatario(u.getId(), u.getNombre(), celular);
        boolean esPlus = u.planEfectivo() == PlanTipo.PLUS;
        for (Canal canal : p.getCanales()) {
            boolean premium = canal == Canal.WHATSAPP || canal == Canal.SMS;
            if (premium && !esPlus) {
                continue;
            }
            try {
                fabrica.obtener(canal).enviar(destino, titulo, mensaje);
            } catch (Exception e) {
                log.warn("Falló el envío por {}: {}", canal, e.getMessage());
            }
        }
    }

    public List<MensajeEnviado> bandeja(String usuarioId) {
        return bandeja.ultimos(usuarioId);
    }

    private static String blankANull(String s) {
        return s == null || s.isBlank() ? null : s;
    }
}
