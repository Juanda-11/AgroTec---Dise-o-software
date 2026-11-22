package com.agrotec.integration.weather;

import com.agrotec.util.Textos;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Adaptador a Open-Meteo (https://open-meteo.com): API pública gratuita, sin llave.
 * Se activa con {@code agrotec.weather.provider=openmeteo}. Si el servicio no responde,
 * cae al proveedor simulado para que la app siga funcionando (resiliencia).
 */
@Component
@ConditionalOnProperty(name = "agrotec.weather.provider", havingValue = "openmeteo")
public class OpenMeteoWeatherProvider implements WeatherProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenMeteoWeatherProvider.class);
    private static final String FUENTE = "Open-Meteo";

    private final RestClient http = RestClient.create("https://api.open-meteo.com");
    private final WeatherProvider respaldo = new SimulatedWeatherProvider();

    @Override
    public Forecast obtener(double latitud, double longitud, int dias) {
        try {
            Map<String, Object> json = http.get()
                    .uri(u -> u.path("/v1/forecast")
                            .queryParam("latitude", latitud)
                            .queryParam("longitude", longitud)
                            .queryParam("current", "temperature_2m,relative_humidity_2m,wind_speed_10m,precipitation")
                            .queryParam("daily", "temperature_2m_max,temperature_2m_min,precipitation_probability_max,"
                                    + "precipitation_sum,wind_speed_10m_max,relative_humidity_2m_mean")
                            .queryParam("timezone", Textos.ZONA.getId())
                            .queryParam("forecast_days", dias)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() { });
            return convertir(json, dias);
        } catch (Exception e) {
            log.warn("Open-Meteo no disponible ({}), usando datos simulados", e.getMessage());
            return respaldo.obtener(latitud, longitud, dias);
        }
    }

    @SuppressWarnings("unchecked")
    private Forecast convertir(Map<String, Object> json, int dias) {
        Map<String, Object> cur = (Map<String, Object>) json.get("current");
        Map<String, Object> daily = (Map<String, Object>) json.get("daily");
        List<String> fechas = (List<String>) daily.get("time");

        List<ForecastDay> lista = new ArrayList<>();
        for (int i = 0; i < fechas.size() && i < dias; i++) {
            lista.add(new ForecastDay(
                    LocalDate.parse(fechas.get(i)),
                    num(daily, "temperature_2m_min", i, 0),
                    num(daily, "temperature_2m_max", i, 0),
                    (int) Math.round(num(daily, "precipitation_probability_max", i, 0)),
                    num(daily, "precipitation_sum", i, 0),
                    num(daily, "wind_speed_10m_max", i, 0),
                    (int) Math.round(num(daily, "relative_humidity_2m_mean", i, 70))));
        }
        int prob = lista.get(0).probLluvia();
        CurrentWeather actual = new CurrentWeather(
                ((Number) cur.get("temperature_2m")).doubleValue(),
                ((Number) cur.get("relative_humidity_2m")).intValue(),
                ((Number) cur.get("wind_speed_10m")).doubleValue(),
                SimulatedWeatherProvider.descripcion(prob));
        return new Forecast(actual, lista, FUENTE, Instant.now());
    }

    @SuppressWarnings("unchecked")
    private static double num(Map<String, Object> daily, String clave, int i, double porDefecto) {
        Object lista = daily.get(clave);
        if (lista instanceof List<?> l && i < l.size() && l.get(i) instanceof Number n) {
            return n.doubleValue();
        }
        return porDefecto;
    }
}
