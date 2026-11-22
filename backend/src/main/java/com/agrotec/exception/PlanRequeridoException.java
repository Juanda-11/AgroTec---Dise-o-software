package com.agrotec.exception;

/** La función requiere el plan Plus (HTTP 403). RF-14. */
public class PlanRequeridoException extends RuntimeException {
    public PlanRequeridoException(String funcion) {
        super("«" + funcion + "» está disponible en el plan Plus. Suscríbete para usarla.");
    }
}
