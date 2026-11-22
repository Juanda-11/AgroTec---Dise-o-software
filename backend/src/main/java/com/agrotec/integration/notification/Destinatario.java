package com.agrotec.integration.notification;

/** A quién se envía una notificación. */
public record Destinatario(String usuarioId, String nombre, String celular) {
}
