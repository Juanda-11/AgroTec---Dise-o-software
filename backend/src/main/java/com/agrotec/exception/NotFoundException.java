package com.agrotec.exception;

/** Recurso inexistente o que no pertenece al usuario (HTTP 404). */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String mensaje) {
        super(mensaje);
    }
}
