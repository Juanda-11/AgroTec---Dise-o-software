package com.agrotec.exception;

/** Credenciales inválidas o cuenta bloqueada (HTTP 401). */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String mensaje) {
        super(mensaje);
    }
}
