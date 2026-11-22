package com.agrotec.exception;

import java.time.Instant;
import java.util.Map;

/** Cuerpo estándar de error de la API. */
public record ApiError(int status, String error, String mensaje, Instant fecha, Map<String, String> detalles) {

    public static ApiError of(int status, String error, String mensaje) {
        return new ApiError(status, error, mensaje, Instant.now(), null);
    }
}
