package com.agrotec.service.rules;

import com.agrotec.domain.NivelRiesgo;
import com.agrotec.domain.TipoAlerta;
import java.time.LocalDate;

/** Resultado de aplicar una regla de riesgo: base para crear una {@code Alerta}. */
public record HallazgoRiesgo(String regla, TipoAlerta tipo, NivelRiesgo nivel, LocalDate fechaEvento,
                             String titulo, String mensaje, String recomendacion) {
}
