package com.agrotec.integration.payment;

import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Pasarela SIMULADA para desarrollo y demostración: no mueve dinero real.
 * Aprueba el pago salvo que el token sea "RECHAZAR" (sirve para probar el flujo de error).
 */
@Component
public class SimulatedPaymentGateway implements PaymentGateway {

    @Override
    public ResultadoPago cobrar(SolicitudPago s) {
        if ("RECHAZAR".equalsIgnoreCase(s.tokenPago())) {
            return new ResultadoPago(false, null, "El pago fue rechazado por la entidad");
        }
        return new ResultadoPago(true, "SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "Pago aprobado");
    }
}
