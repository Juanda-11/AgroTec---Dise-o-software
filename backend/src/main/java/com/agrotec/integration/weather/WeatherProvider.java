package com.agrotec.integration.weather;

/**
 * Puerto de salida hacia un servicio meteorológico (patrón ADAPTER).
 * El resto del sistema depende de esta interfaz, no de un proveedor concreto: se puede cambiar
 * Open-Meteo por IDEAM sin tocar la lógica de negocio.
 */
public interface WeatherProvider {

    /** Pronóstico para una coordenada con {@code dias} días (1 a 7). */
    Forecast obtener(double latitud, double longitud, int dias);
}
