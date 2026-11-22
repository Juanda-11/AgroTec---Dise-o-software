package com.agrotec.service;

import java.time.Instant;

/** Regla de resolución de conflictos de sincronización: gana el cambio más reciente (last-write-wins). */
public final class SyncMerge {

    private SyncMerge() {
    }

    /** true si el cambio del celular debe reemplazar al del servidor. */
    public static boolean clienteGana(Instant cliente, Instant servidor) {
        return servidor == null || !cliente.isBefore(servidor);
    }

    /** Evita que un reloj mal configurado en el celular guarde fechas del futuro. */
    public static Instant limitar(Instant cliente, Instant ahora) {
        return cliente.isAfter(ahora) ? ahora : cliente;
    }
}
