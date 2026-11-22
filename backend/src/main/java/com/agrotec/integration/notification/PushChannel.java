package com.agrotec.integration.notification;

import com.agrotec.domain.Canal;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Notificación push dentro de la app. En producción se integraría con Firebase Cloud Messaging. */
@Component
public class PushChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(PushChannel.class);
    private final BandejaSimulada bandeja;

    public PushChannel(BandejaSimulada bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public Canal canal() {
        return Canal.PUSH;
    }

    @Override
    public void enviar(Destinatario d, String titulo, String mensaje) {
        log.info("[PUSH -> {}] {}", d.nombre(), titulo);
        bandeja.registrar(d.usuarioId(), new MensajeEnviado(Instant.now(), Canal.PUSH, "app", titulo, mensaje));
    }
}
