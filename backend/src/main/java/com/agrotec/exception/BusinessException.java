package com.agrotec.exception;

/** Regla de negocio incumplida o dato inválido (HTTP 400). */
public class BusinessException extends RuntimeException {
    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
