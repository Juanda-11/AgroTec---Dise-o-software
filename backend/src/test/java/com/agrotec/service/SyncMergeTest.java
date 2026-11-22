package com.agrotec.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class SyncMergeTest {

    private final Instant t0 = Instant.parse("2025-11-20T10:00:00Z");

    @Test
    void ganaElCambioMasReciente() {
        assertTrue(SyncMerge.clienteGana(t0.plusSeconds(60), t0));
        assertFalse(SyncMerge.clienteGana(t0.minusSeconds(60), t0));
    }

    @Test
    void sinRegistroEnElServidorGanaElCliente() {
        assertTrue(SyncMerge.clienteGana(t0, null));
    }

    @Test
    void noPermiteFechasDelFuturo() {
        assertEquals(t0, SyncMerge.limitar(t0.plusSeconds(3600), t0));
        assertEquals(t0.minusSeconds(5), SyncMerge.limitar(t0.minusSeconds(5), t0));
    }
}
