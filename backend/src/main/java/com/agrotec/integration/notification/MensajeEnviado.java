package com.agrotec.integration.notification;

import com.agrotec.domain.Canal;
import java.time.Instant;

/** Mensaje registrado en la bandeja simulada. */
public record MensajeEnviado(Instant fecha, Canal canal, String destino, String titulo, String mensaje) {
}
