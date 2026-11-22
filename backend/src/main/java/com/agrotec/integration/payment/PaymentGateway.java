package com.agrotec.integration.payment;

/** Puerto hacia la pasarela de pagos (patrón ADAPTER): Wompi, PayU, Mercado Pago, etc. */
public interface PaymentGateway {

    ResultadoPago cobrar(SolicitudPago solicitud);
}
