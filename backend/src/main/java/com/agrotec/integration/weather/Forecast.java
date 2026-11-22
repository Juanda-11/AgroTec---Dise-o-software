package com.agrotec.integration.weather;

import java.time.Instant;
import java.util.List;

/** Respuesta completa de un proveedor meteorológico. {@code fuente} se muestra al usuario (RNF-06). */
public record Forecast(CurrentWeather actual, List<ForecastDay> dias, String fuente, Instant obtenidoEn) {
}
