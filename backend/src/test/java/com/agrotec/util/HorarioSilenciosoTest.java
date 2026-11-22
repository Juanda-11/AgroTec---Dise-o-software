package com.agrotec.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class HorarioSilenciosoTest {

    @Test
    void rangoQueCruzaLaMedianoche() {
        assertTrue(HorarioSilencioso.estaEnSilencio(LocalTime.of(22, 0), "21:00", "05:00"));
        assertTrue(HorarioSilencioso.estaEnSilencio(LocalTime.of(3, 30), "21:00", "05:00"));
        assertFalse(HorarioSilencioso.estaEnSilencio(LocalTime.of(5, 0), "21:00", "05:00"));
        assertFalse(HorarioSilencioso.estaEnSilencio(LocalTime.of(12, 0), "21:00", "05:00"));
    }

    @Test
    void rangoEnElMismoDia() {
        assertTrue(HorarioSilencioso.estaEnSilencio(LocalTime.of(13, 0), "12:00", "14:00"));
        assertFalse(HorarioSilencioso.estaEnSilencio(LocalTime.of(14, 0), "12:00", "14:00"));
    }

    @Test
    void sinConfiguracionNoHaySilencio() {
        assertFalse(HorarioSilencioso.estaEnSilencio(LocalTime.of(23, 0), null, null));
        assertFalse(HorarioSilencioso.estaEnSilencio(LocalTime.of(23, 0), "", ""));
        assertFalse(HorarioSilencioso.estaEnSilencio(LocalTime.of(23, 0), "21:00", "21:00"));
    }
}
