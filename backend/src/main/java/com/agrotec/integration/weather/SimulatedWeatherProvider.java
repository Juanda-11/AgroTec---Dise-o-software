package com.agrotec.integration.weather;

import com.agrotec.util.Textos;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Proveedor SIMULADO: genera un pronóstico creíble y determinista para la zona andina de Nariño.
 * Sirve para demostraciones, pruebas y como respaldo cuando el proveedor real no responde.
 * No representa datos meteorológicos reales.
 */
@Component
@ConditionalOnProperty(name = "agrotec.weather.provider", havingValue = "simulated", matchIfMissing = true)
public class SimulatedWeatherProvider implements WeatherProvider {

    public static final String FUENTE = "Simulado (demostración)";

    @Override
    public Forecast obtener(double latitud, double longitud, int dias) {
        LocalDate hoy = LocalDate.now(Textos.ZONA);
        List<ForecastDay> lista = new ArrayList<>();
        for (int i = 0; i < dias; i++) {
            lista.add(dia(latitud, longitud, hoy.plusDays(i)));
        }
        ForecastDay d0 = lista.get(0);
        CurrentWeather actual = new CurrentWeather(
                Math.round(((d0.tempMin() + d0.tempMax()) / 2) * 10) / 10.0,
                d0.humedad(), d0.vientoKmh(), descripcion(d0.probLluvia()));
        return new Forecast(actual, lista, FUENTE, Instant.now());
    }

    private ForecastDay dia(double lat, double lon, LocalDate fecha) {
        Random r = new Random(Objects.hash(Math.round(lat * 10), Math.round(lon * 10), fecha.toEpochDay()));
        double tMax = 16 + r.nextDouble() * 5;
        double tMin = 4 + r.nextDouble() * 5;
        if (r.nextInt(8) == 0) {
            tMin = r.nextDouble() * 3; // de vez en cuando hay heladas en zonas altas
        }
        int prob = r.nextInt(100);
        double mm = prob > 60 ? (prob - 60) * r.nextDouble() * 1.3 : 0;
        double viento = 6 + r.nextDouble() * 16;
        int humedad = (int) Math.min(98, 60 + prob * 0.3 + r.nextInt(10));
        return new ForecastDay(fecha, redondear(tMin), redondear(tMax), prob, redondear(mm), redondear(viento), humedad);
    }

    static String descripcion(int probLluvia) {
        if (probLluvia < 30) {
            return "Despejado";
        }
        return probLluvia < 60 ? "Nublado" : "Lluvioso";
    }

    private static double redondear(double v) {
        return Math.round(v * 10) / 10.0;
    }
}
