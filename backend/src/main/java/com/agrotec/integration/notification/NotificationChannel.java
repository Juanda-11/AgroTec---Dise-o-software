package com.agrotec.integration.notification;

import com.agrotec.domain.Canal;

/**
 * Canal de entrega de notificaciones (patrón STRATEGY): cada implementación sabe cómo enviar por
 * un medio distinto (push, WhatsApp, SMS) y todas se usan de la misma forma.
 */
public interface NotificationChannel {

    Canal canal();

    void enviar(Destinatario destinatario, String titulo, String mensaje);
}
