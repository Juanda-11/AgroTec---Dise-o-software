package com.agrotec.integration.notification;

import com.agrotec.domain.Canal;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Mensaje por WhatsApp. En producción se integraría con la API de WhatsApp Business (Cloud API). */
@Component
public class WhatsAppChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppChannel.class);
    private final BandejaSimulada bandeja;

    public WhatsAppChannel(BandejaSimulada bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public Canal canal() {
        return Canal.WHATSAPP;
    }

    @Override
    public void enviar(Destinatario d, String titulo, String mensaje) {
        log.info("[WHATSAPP -> {}] {}", d.celular(), titulo);
        bandeja.registrar(d.usuarioId(), new MensajeEnviado(Instant.now(), Canal.WHATSAPP, d.celular(), titulo, mensaje));
    }
}
