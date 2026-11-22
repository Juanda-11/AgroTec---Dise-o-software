package com.agrotec.util;

import java.time.LocalTime;

/** Lógica pura del horario silencioso (HU de preferencias de notificación). */
public final class HorarioSilencioso {

    private HorarioSilencioso() {
    }

    /**
     * Indica si la hora {@code ahora} cae dentro del rango [inicio, fin).
     * Soporta rangos que cruzan la medianoche (21:00 - 05:00).
     * Si inicio o fin son nulos/vacíos, no hay horario silencioso.
     */
    public static boolean estaEnSilencio(LocalTime ahora, String inicio, String fin) {
        if (inicio == null || fin == null || inicio.isBlank() || fin.isBlank()) {
            return false;
        }
        LocalTime i = LocalTime.parse(inicio);
        LocalTime f = LocalTime.parse(fin);
        if (i.equals(f)) {
            return false;
        }
        if (i.isBefore(f)) {
            return !ahora.isBefore(i) && ahora.isBefore(f);
        }
        return !ahora.isBefore(i) || ahora.isBefore(f);
    }
}
