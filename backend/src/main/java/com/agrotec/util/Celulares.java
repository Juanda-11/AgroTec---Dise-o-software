package com.agrotec.util;

import com.agrotec.exception.BusinessException;

/** Utilidad para normalizar números de celular colombianos (10 dígitos que empiezan por 3). */
public final class Celulares {

    private Celulares() {
    }

    /**
     * Acepta "+57 300 000 0000", "300-000-0000", "3000000000" y devuelve "3000000000".
     *
     * @throws BusinessException si el número no tiene formato de celular colombiano
     */
    public static String normalizar(String crudo) {
        if (crudo == null) {
            throw new BusinessException("El número de celular es obligatorio");
        }
        String digitos = crudo.replaceAll("\\D", "");
        if (digitos.length() == 12 && digitos.startsWith("57")) {
            digitos = digitos.substring(2);
        }
        if (!digitos.matches("3\\d{9}")) {
            throw new BusinessException("El celular debe tener 10 dígitos y empezar por 3 (ej. 300 000 0000)");
        }
        return digitos;
    }
}
