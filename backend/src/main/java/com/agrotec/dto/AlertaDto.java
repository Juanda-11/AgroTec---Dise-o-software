package com.agrotec.dto;

import com.agrotec.domain.Alerta;
import com.agrotec.domain.NivelRiesgo;
import com.agrotec.domain.TipoAlerta;
import java.time.Instant;

public final class AlertaDto {

    private AlertaDto() {
    }

    public record Response(String id, TipoAlerta tipo, NivelRiesgo nivel, String titulo, String mensaje,
                           String recomendacion, String cultivoId, String fuente, Instant creadaEn, boolean leida) {
        public static Response de(Alerta a) {
            return new Response(a.getId(), a.getTipo(), a.getNivel(), a.getTitulo(), a.getMensaje(),
                    a.getRecomendacion(), a.getCultivoId(), a.getFuente(), a.getCreadaEn(), a.isLeida());
        }
    }
}
