package com.agrotec.dto;

import com.agrotec.domain.Canal;
import com.agrotec.domain.PreferenciasNotificacion;
import com.agrotec.domain.TipoAlerta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Set;

public final class PreferenciasDto {

    private PreferenciasDto() {
    }

    public record Request(
            @NotNull(message = "Indica los canales") Set<Canal> canales,
            @NotNull(message = "Indica los tipos de alerta") Set<TipoAlerta> tipos,
            String numeroWhatsapp,
            @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Usa el formato HH:mm") String silencioInicio,
            @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Usa el formato HH:mm") String silencioFin) {
    }

    public record Response(Set<Canal> canales, Set<TipoAlerta> tipos, String numeroWhatsapp, String silencioInicio,
                           String silencioFin) {
        public static Response de(PreferenciasNotificacion p) {
            return new Response(p.getCanales(), p.getTipos(), p.getNumeroWhatsapp(), p.getSilencioInicio(),
                    p.getSilencioFin());
        }
    }
}
