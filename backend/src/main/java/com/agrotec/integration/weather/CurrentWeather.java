package com.agrotec.integration.weather;

/** Condiciones actuales en la finca. */
public record CurrentWeather(double temperatura, int humedad, double vientoKmh, String descripcion) {
}
