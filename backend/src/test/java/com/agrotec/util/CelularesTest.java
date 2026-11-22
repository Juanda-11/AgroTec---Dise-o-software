package com.agrotec.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.agrotec.exception.BusinessException;
import org.junit.jupiter.api.Test;

class CelularesTest {

    @Test
    void normalizaFormatosComunes() {
        assertEquals("3001234567", Celulares.normalizar("300 123 4567"));
        assertEquals("3001234567", Celulares.normalizar("+57 300-123-4567"));
        assertEquals("3001234567", Celulares.normalizar("3001234567"));
    }

    @Test
    void rechazaNumerosInvalidos() {
        assertThrows(BusinessException.class, () -> Celulares.normalizar("1234567"));
        assertThrows(BusinessException.class, () -> Celulares.normalizar("2001234567"));
        assertThrows(BusinessException.class, () -> Celulares.normalizar(null));
    }
}
