package com.agrotec.dto;

import com.agrotec.domain.PlanTipo;
import com.agrotec.integration.weather.CurrentWeather;
import com.agrotec.integration.weather.ForecastDay;
import java.time.Instant;
import java.util.List;

public final class ClimaDto {

    private ClimaDto() {
    }

    /**
     * Pronóstico de la finca. {@code desactualizado} es true si los datos tienen más de 24 horas (RNF-06);
     * {@code diasMostrados} depende del plan: 3 días en Gratis, 7 en Plus.
     */
    public record Response(String ubicacion, CurrentWeather actual, List<ForecastDay> dias, String fuente,
                           Instant actualizadoEn, boolean desactualizado, PlanTipo plan, int diasMostrados) {
    }
}
