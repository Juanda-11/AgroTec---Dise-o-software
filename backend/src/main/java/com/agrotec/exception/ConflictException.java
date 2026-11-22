package com.agrotec.exception;

/** Conflicto con el estado actual, p. ej. celular ya registrado (HTTP 409). */
public class ConflictException extends RuntimeException {
    public ConflictException(String mensaje) {
        super(mensaje);
    }
}
