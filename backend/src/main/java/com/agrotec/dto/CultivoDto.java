package com.agrotec.dto;

import com.agrotec.domain.Cultivo;
import com.agrotec.domain.EstadoCultivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;

public final class CultivoDto {

    private CultivoDto() {
    }

    public record Request(
            @Size(max = 64) String clienteId,
            @NotBlank(message = "El tipo de cultivo es obligatorio") @Size(max = 60) String tipo,
            @NotBlank(message = "El nombre del lote es obligatorio") @Size(max = 60) String lote,
            @Size(max = 60) String variedad,
            @Positive(message = "El área debe ser mayor que cero") Double areaHa,
            @NotNull(message = "La fecha de siembra es obligatoria") LocalDate fechaSiembra,
            LocalDate fechaCosechaEstimada,
            @Size(max = 500) String notas) {
    }

    public record Response(String id, String clienteId, String tipo, String lote, String variedad, Double areaHa,
                           LocalDate fechaSiembra, LocalDate fechaCosechaEstimada, EstadoCultivo estado,
                           String notas, Instant actualizadoEn) {
        public static Response de(Cultivo c) {
            return new Response(c.getId(), c.getClienteId(), c.getTipo(), c.getLote(), c.getVariedad(), c.getAreaHa(),
                    c.getFechaSiembra(), c.getFechaCosechaEstimada(), c.getEstado(), c.getNotas(),
                    c.getActualizadoEn());
        }
    }
}
