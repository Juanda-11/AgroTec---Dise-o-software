package com.agrotec.dto;

import com.agrotec.domain.AlertaPrecio;
import com.agrotec.domain.CategoriaPrecio;
import com.agrotec.domain.Tendencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

public final class PrecioDto {

    private PrecioDto() {
    }

    public record Item(String producto, CategoriaPrecio categoria, String plaza, String unidad, long precio,
                       double variacionPct, Tendencia tendencia, LocalDate fecha, String fuente,
                       boolean desactualizado) {
    }

    public record Punto(LocalDate fecha, long precio) {
    }

    public record TendenciaResponse(String producto, String plaza, String unidad, List<Punto> puntos, String fuente) {
    }

    /** Carga de precios por un administrador (simula la integración con SIPSA-DANE). */
    public record IngestaRequest(
            @NotBlank String producto,
            @NotNull CategoriaPrecio categoria,
            @NotBlank String plaza,
            @NotBlank String unidad,
            @Positive long precio,
            @NotNull LocalDate fecha,
            @NotBlank String fuente) {
    }

    public record AlertaRequest(
            @NotBlank(message = "El producto es obligatorio") String producto,
            @NotBlank(message = "La plaza es obligatoria") String plaza,
            @Positive(message = "El precio objetivo debe ser mayor que cero") long precioObjetivo) {
    }

    public record AlertaResponse(String id, String producto, String plaza, long precioObjetivo, boolean activa) {
        public static AlertaResponse de(AlertaPrecio a) {
            return new AlertaResponse(a.getId(), a.getProducto(), a.getPlaza(), a.getPrecioObjetivo(), a.isActiva());
        }
    }
}
