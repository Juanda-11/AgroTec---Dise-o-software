package com.agrotec.dto;

import com.agrotec.domain.MetodoPago;
import com.agrotec.domain.PlanTipo;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public final class SuscripcionDto {

    private SuscripcionDto() {
    }

    public record PlanInfo(PlanTipo plan, String nombre, long precioMensual, List<String> beneficios) {
    }

    public record SuscribirRequest(
            @NotNull(message = "Elige un plan") PlanTipo plan,
            @NotNull(message = "Elige un método de pago") MetodoPago metodoPago,
            /** Token que entrega la pasarela (nunca se reciben números de tarjeta en esta API). */
            String tokenPago) {
    }

    public record Estado(PlanTipo plan, Instant vigenteHasta, boolean cancelada, String mensaje) {
    }
}
