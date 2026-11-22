package com.agrotec.service.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agrotec.domain.NivelRiesgo;
import com.agrotec.integration.weather.ForecastDay;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReglasClimaticasTest {

    private static final LocalDate HOY = LocalDate.of(2025, 11, 20);

    private static ForecastDay dia(int offset, double tMin, double tMax, int prob, double mm, double viento) {
        return new ForecastDay(HOY.plusDays(offset), tMin, tMax, prob, mm, viento, 75);
    }

    private static List<ForecastDay> semanaTranquila() {
        List<ForecastDay> l = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            l.add(dia(i, 8, 18, 40, 2, 10));
        }
        return l;
    }

    @Test
    void sinRiesgoNoGeneraHallazgos() {
        assertTrue(ReglasClimaticas.evaluar(semanaTranquila()).isEmpty());
    }

    @Test
    void heladaConTemperaturaBajoCeroEsRiesgoAlto() {
        List<ForecastDay> dias = semanaTranquila();
        dias.set(0, dia(0, -1, 17, 20, 0, 8));
        List<HallazgoRiesgo> r = ReglasClimaticas.evaluar(dias);
        assertEquals(1, r.size());
        assertEquals("helada", r.get(0).regla());
        assertEquals(NivelRiesgo.ALTO, r.get(0).nivel());
        assertEquals(HOY, r.get(0).fechaEvento());
    }

    @Test
    void heladaLeveEsRiesgoMedio() {
        List<ForecastDay> dias = semanaTranquila();
        dias.set(1, dia(1, 2.5, 17, 20, 0, 8));
        List<HallazgoRiesgo> r = ReglasClimaticas.evaluar(dias);
        assertEquals(NivelRiesgo.MEDIO, r.get(0).nivel());
        assertTrue(r.get(0).titulo().contains("mañana"));
    }

    @Test
    void lluviaIntensaEsRiesgoAlto() {
        List<ForecastDay> dias = semanaTranquila();
        dias.set(0, dia(0, 9, 16, 95, 42, 10));
        List<HallazgoRiesgo> r = ReglasClimaticas.evaluar(dias);
        assertEquals("lluvia", r.get(0).regla());
        assertEquals(NivelRiesgo.ALTO, r.get(0).nivel());
    }

    @Test
    void cincoDiasSecosGeneranAlertaDeSequia() {
        List<ForecastDay> dias = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            dias.add(dia(i, 9, 22, 5, 0, 10));
        }
        List<HallazgoRiesgo> r = ReglasClimaticas.evaluar(dias);
        assertTrue(r.stream().anyMatch(h -> h.regla().equals("sequia")));
    }

    @Test
    void soloEvaluaHoyYMananaParaEventosPuntuales() {
        List<ForecastDay> dias = semanaTranquila();
        dias.set(4, dia(4, -3, 15, 20, 0, 8)); // helada en 4 días: aún no se alerta
        assertTrue(ReglasClimaticas.evaluar(dias).isEmpty());
    }
}
