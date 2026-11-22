package com.agrotec.dto;

import com.agrotec.domain.Actividad;
import com.agrotec.domain.TipoActividad;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;

public final class ActividadDto {

    private ActividadDto() {
    }

    public record Request(
            @Size(max = 64) String clienteId,
            @NotNull(message = "El tipo de actividad es obligatorio") TipoActividad tipo,
            @NotNull(message = "La fecha es obligatoria") LocalDate fecha,
            @Size(max = 500) String nota,
            Instant recordatorio) {
    }

    public record Response(String id, String cultivoId, String clienteId, TipoActividad tipo, LocalDate fecha,
                           String nota, Instant recordatorio, boolean completada, Instant actualizadoEn) {
        public static Response de(Actividad a) {
            return new Response(a.getId(), a.getCultivoId(), a.getClienteId(), a.getTipo(), a.getFecha(), a.getNota(),
                    a.getRecordatorio(), a.isCompletada(), a.getActualizadoEn());
        }
    }
}
