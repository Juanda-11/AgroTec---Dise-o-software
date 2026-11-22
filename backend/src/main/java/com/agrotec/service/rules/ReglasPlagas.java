package com.agrotec.service.rules;

import com.agrotec.domain.NivelRiesgo;
import com.agrotec.domain.TipoAlerta;
import com.agrotec.integration.weather.ForecastDay;
import com.agrotec.util.Textos;
import java.util.ArrayList;
import java.util.List;

/**
 * Reglas de ejemplo (RF-07): relacionan el clima de los próximos 2 días con enfermedades y plagas
 * típicas de cada cultivo. Son una aproximación didáctica; en producción deben validarse con el ICA,
 * Fedecafé o un ingeniero agrónomo.
 */
public final class ReglasPlagas {

    private ReglasPlagas() {
    }

    public static List<HallazgoRiesgo> evaluar(String tipoCultivo, List<ForecastDay> dias) {
        List<HallazgoRiesgo> r = new ArrayList<>();
        if (dias.isEmpty()) {
            return r;
        }
        List<ForecastDay> proximos = dias.subList(0, Math.min(2, dias.size()));
        double humedad = proximos.stream().mapToInt(ForecastDay::humedad).average().orElse(0);
        int lluvia = proximos.stream().mapToInt(ForecastDay::probLluvia).max().orElse(0);
        double tMin = proximos.stream().mapToDouble(ForecastDay::tempMin).min().orElse(0);
        double tMax = proximos.stream().mapToDouble(ForecastDay::tempMax).max().orElse(0);
        var fecha = proximos.get(0).fecha();

        switch (Textos.normalizar(tipoCultivo)) {
            case "cafe" -> {
                if (humedad >= 80 && lluvia >= 60) {
                    r.add(new HallazgoRiesgo("roya", TipoAlerta.PLAGA, humedad >= 90 ? NivelRiesgo.ALTO : NivelRiesgo.MEDIO,
                            fecha, "Riesgo de roya del café",
                            "La humedad alta y la lluvia favorecen el desarrollo de la roya.",
                            "Revisa el envés de las hojas y consulta con tu asesor el control más adecuado."));
                }
                if (humedad <= 70 && tMax >= 22) {
                    r.add(new HallazgoRiesgo("broca", TipoAlerta.PLAGA, NivelRiesgo.MEDIO, fecha,
                            "Riesgo de broca del café",
                            "El tiempo seco y cálido favorece la reproducción de la broca.",
                            "Revisa frutos, realiza el re-reco y mantén limpio el cafetal."));
                }
            }
            case "papa" -> {
                if (humedad >= 85 && lluvia >= 60 && tMin >= 7 && tMin <= 15) {
                    r.add(new HallazgoRiesgo("gota", TipoAlerta.PLAGA, NivelRiesgo.ALTO, fecha,
                            "Riesgo de gota de la papa",
                            "Humedad alta, lluvia y noches frescas favorecen la gota (Phytophthora infestans).",
                            "Inspecciona el follaje y consulta el plan de aplicaciones preventivas con tu asesor."));
                }
            }
            case "maiz" -> {
                if (humedad >= 85) {
                    r.add(new HallazgoRiesgo("pudricion", TipoAlerta.PLAGA, NivelRiesgo.MEDIO, fecha,
                            "Riesgo de pudrición de mazorca",
                            "La humedad muy alta favorece hongos en la mazorca.",
                            "Mejora la aireación del lote y revisa las mazorcas."));
                }
            }
            case "frijol" -> {
                if (humedad >= 85 && lluvia >= 60) {
                    r.add(new HallazgoRiesgo("antracnosis", TipoAlerta.PLAGA, NivelRiesgo.MEDIO, fecha,
                            "Riesgo de antracnosis del fríjol",
                            "La humedad y la lluvia favorecen la antracnosis.",
                            "Evita trabajar el cultivo mojado y retira plantas enfermas."));
                }
            }
            default -> { }
        }
        return r;
    }
}
