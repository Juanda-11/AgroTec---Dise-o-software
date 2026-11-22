package com.agrotec.dto;

import com.agrotec.domain.Finca;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class PerfilDto {

    private PerfilDto() {
    }

    public record ActualizarRequest(@NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String nombre) {
    }

    public record FincaRequest(
            @NotBlank(message = "El nombre de la finca es obligatorio") @Size(max = 100) String nombre,
            @Size(max = 60) String departamento,
            @NotBlank(message = "El municipio es obligatorio") @Size(max = 60) String municipio,
            @Size(max = 80) String vereda,
            @DecimalMin(value = "-90", message = "Latitud inválida") @DecimalMax(value = "90", message = "Latitud inválida") Double latitud,
            @DecimalMin(value = "-180", message = "Longitud inválida") @DecimalMax(value = "180", message = "Longitud inválida") Double longitud) {
    }

    public record FincaResponse(String id, String nombre, String departamento, String municipio, String vereda,
                                Double latitud, Double longitud, Instant actualizadoEn) {
        public static FincaResponse de(Finca f) {
            if (f == null) {
                return null;
            }
            return new FincaResponse(f.getId(), f.getNombre(), f.getDepartamento(), f.getMunicipio(), f.getVereda(),
                    f.getLatitud(), f.getLongitud(), f.getActualizadoEn());
        }
    }

    public record PerfilResponse(UsuarioResponse usuario, FincaResponse finca) {
    }
}
