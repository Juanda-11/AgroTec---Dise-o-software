package com.agrotec.integration.weather;

import java.time.LocalDate;

/** Pronóstico de un día: temperaturas en °C, probabilidad de lluvia en %, lluvia en mm, viento en km/h. */
public record ForecastDay(LocalDate fecha, double tempMin, double tempMax, int probLluvia, double lluviaMm,
                          double vientoKmh, int humedad) {
}
