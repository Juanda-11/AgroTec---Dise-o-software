package com.agrotec.event;

import com.agrotec.domain.Alerta;

/** Evento de dominio (patrón OBSERVER): se publica cuando se crea una alerta nueva. */
public record AlertaCreadaEvent(Alerta alerta) {
}
