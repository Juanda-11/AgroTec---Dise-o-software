package com.agrotec.integration.payment;

import com.agrotec.domain.MetodoPago;

public record SolicitudPago(String usuarioId, long monto, MetodoPago metodo, String tokenPago) {
}
