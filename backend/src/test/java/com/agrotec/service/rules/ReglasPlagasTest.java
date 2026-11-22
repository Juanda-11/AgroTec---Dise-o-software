package com.agrotec.service.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agrotec.domain.NivelRiesgo;
import com.agrotec.domain.TipoAlerta;
import com.agrotec.integration.weather.ForecastDay;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReglasPlagasTest {

    private static final LocalDate HOY = LocalDate.of(2025, 11, 20);

    private static List<ForecastDay> dias(int humedad, int prob, double tMin, double tMax) {
        return List.of(new ForecastDay(HOY, tMin, tMax, prob, 5, 10, humedad),
                new ForecastDay(HOY.plusDays(1), tMin, tMax, prob, 5, 10, humedad));
    }

    @Test
    void royaDelCafeConHumedadYLluviaAltas() {
        List<HallazgoRiesgo> r = ReglasPlagas.evaluar("Café", dias(92, 80, 12, 20));
        assertEquals(1, r.size());
        assertEquals("roya", r.get(0).regla());
        assertEquals(NivelRiesgo.ALTO, r.get(0).nivel());
        assertEquals(TipoAlerta.PLAGA, r.get(0).tipo());
    }

    @Test
    void brocaConTiempoSecoYCalido() {
        List<HallazgoRiesgo> r = ReglasPlagas.evaluar("cafe", dias(60, 10, 14, 25));
        assertEquals("broca", r.get(0).regla());
    }

    @Test
    void gotaDeLaPapa() {
        List<HallazgoRiesgo> r = ReglasPlagas.evaluar("Papa", dias(90, 75, 9, 16));
        assertEquals("gota", r.get(0).regla());
    }

    @Test
    void cultivoSinReglasNoGeneraAlertas() {
        assertTrue(ReglasPlagas.evaluar("Aguacate", dias(95, 90, 10, 20)).isEmpty());
    }

    @Test
    void ignoraTildesYMayusculas() {
        assertEquals(1, ReglasPlagas.evaluar("FRÍJOL", dias(90, 80, 12, 20)).size());
    }
}
