package com.agrotec.integration.notification;

import com.agrotec.domain.Canal;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Mensaje de texto. En producción se integraría con un proveedor de SMS (p. ej. Twilio o Infobip). */
@Component
public class SmsChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(SmsChannel.class);
    private final BandejaSimulada bandeja;

    public SmsChannel(BandejaSimulada bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public Canal canal() {
        return Canal.SMS;
    }

    @Override
    public void enviar(Destinatario d, String titulo, String mensaje) {
        log.info("[SMS -> {}] {}", d.celular(), titulo);
        bandeja.registrar(d.usuarioId(), new MensajeEnviado(Instant.now(), Canal.SMS, d.celular(), titulo, mensaje));
    }
}
