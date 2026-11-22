package com.agrotec.util;

import java.util.Map;
import java.util.Optional;

/**
 * Coordenadas aproximadas de municipios de Nariño. Permiten que el agricultor solo elija su municipio
 * (sin GPS) y aun así reciba el clima de su zona. Son valores aproximados del casco urbano.
 */
public final class MunicipiosNarino {

    public record Punto(double latitud, double longitud) {
    }

    private static final Map<String, Punto> PUNTOS = Map.ofEntries(
            Map.entry("pasto", new Punto(1.2136, -77.2811)),
            Map.entry("ipiales", new Punto(0.8303, -77.6444)),
            Map.entry("tumaco", new Punto(1.7986, -78.8156)),
            Map.entry("tuquerres", new Punto(1.0864, -77.6178)),
            Map.entry("samaniego", new Punto(1.3372, -77.5944)),
            Map.entry("la union", new Punto(1.6014, -77.1306)),
            Map.entry("sandona", new Punto(1.2836, -77.4719)),
            Map.entry("buesaco", new Punto(1.3814, -77.1558)),
            Map.entry("consaca", new Punto(1.2064, -77.4650)),
            Map.entry("yacuanquer", new Punto(1.1167, -77.4000)),
            Map.entry("cumbal", new Punto(0.9019, -77.7928)),
            Map.entry("guachucal", new Punto(0.9619, -77.7311)),
            Map.entry("la cruz", new Punto(1.6039, -76.9758)),
            Map.entry("chachagui", new Punto(1.3600, -77.2833)));

    private MunicipiosNarino() {
    }

    public static Optional<Punto> buscar(String municipio) {
        return Optional.ofNullable(PUNTOS.get(Textos.normalizar(municipio)));
    }
}
