package com.agrotec.util;

import java.text.Normalizer;
import java.time.ZoneId;
import java.util.Locale;

/** Utilidades de texto y constantes comunes. */
public final class Textos {

    /** Zona horaria de Colombia, usada para fechas y horario silencioso. */
    public static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private Textos() {
    }

    /** Minúsculas, sin tildes y sin espacios sobrantes: "Fríjol " -> "frijol". */
    public static String normalizar(String s) {
        if (s == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.trim().toLowerCase(Locale.ROOT);
    }
}
