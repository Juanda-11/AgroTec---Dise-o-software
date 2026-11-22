package com.agrotec.dto;

import com.agrotec.domain.PlanTipo;
import com.agrotec.integration.weather.CurrentWeather;
import java.time.Instant;
import java.util.List;

public final class InicioDto {

    private InicioDto() {
    }

    /** Resumen del día para el panel de inicio (RF-15). Los bloques sin datos llegan en null. */
    public record Response(String nombre, boolean tieneFinca, String ubicacion, CurrentWeather clima,
                           Instant climaActualizadoEn, long alertasSinLeer, AlertaDto.Response ultimaAlerta,
                           List<CultivoDto.Response> cultivos, PrecioDto.Item precioReferencia, PlanTipo plan) {
    }
}
