package com.agrotec.event;

import com.agrotec.repository.UsuarioRepository;
import com.agrotec.service.NotificacionService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Observador del evento {@link AlertaCreadaEvent}: al crearse una alerta, la envía por los canales
 * que el agricultor eligió. AlertaService no conoce a este componente (bajo acoplamiento).
 */
@Component
public class AlertaCreadaListener {

    private final UsuarioRepository usuarios;
    private final NotificacionService notificaciones;

    public AlertaCreadaListener(UsuarioRepository usuarios, NotificacionService notificaciones) {
        this.usuarios = usuarios;
        this.notificaciones = notificaciones;
    }

    @EventListener
    public void alCrearse(AlertaCreadaEvent evento) {
        usuarios.findById(evento.alerta().getUsuarioId())
                .ifPresent(u -> notificaciones.notificarAlerta(u, evento.alerta()));
    }
}
