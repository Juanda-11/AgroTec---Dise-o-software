package com.agrotec.dto;

import com.agrotec.domain.FormatoCapacitacion;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public final class CapacitacionDto {

    private CapacitacionDto() {
    }

    public record Response(String id, String titulo, String descripcion, String cultivo, FormatoCapacitacion formato,
                           int duracionMin, String nivel, String entidad, String url, boolean descargable,
                           int avance) {
    }

    public record AvanceRequest(
            @Min(value = 0, message = "El avance mínimo es 0") @Max(value = 100, message = "El avance máximo es 100") int porcentaje) {
    }
}
