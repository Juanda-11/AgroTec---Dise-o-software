package com.agrotec.service.rules;

import com.agrotec.domain.NivelRiesgo;
import com.agrotec.domain.TipoAlerta;
import com.agrotec.integration.weather.ForecastDay;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Reglas de ejemplo para detectar eventos climáticos de riesgo (RF-06). Son lógica pura (sin Spring ni
 * base de datos), por eso se prueban con pruebas unitarias. Los umbrales deben validarse con un agrónomo.
 */
public final class ReglasClimaticas {

    public static final double HELADA_C = 3.0;
    public static final double LLUVIA_ALTA_MM = 30.0;
    public static final double LLUVIA_MEDIA_MM = 15.0;
    public static final double VIENTO_FUERTE_KMH = 40.0;

    private ReglasClimaticas() {
    }

    /** Evalúa hoy y mañana (heladas, lluvia, viento) y la tendencia de 5 días (sequía). */
    public static List<HallazgoRiesgo> evaluar(List<ForecastDay> dias) {
        List<HallazgoRiesgo> r = new ArrayList<>();
        for (int i = 0; i < Math.min(2, dias.size()); i++) {
            ForecastDay d = dias.get(i);
            String cuando = i == 0 ? "hoy" : "mañana";

            if (d.tempMin() <= HELADA_C) {
                NivelRiesgo nivel = d.tempMin() <= 0 ? NivelRiesgo.ALTO : NivelRiesgo.MEDIO;
                r.add(new HallazgoRiesgo("helada", TipoAlerta.CLIMA, nivel, d.fecha(),
                        "Helada probable " + cuando,
                        String.format(Locale.US, "Se espera una temperatura mínima de %.0f °C %s.", d.tempMin(), cuando),
                        "Cubre las plántulas con plástico o paja y evita regar al final de la tarde."));
            }
            if (d.lluviaMm() >= LLUVIA_ALTA_MM) {
                r.add(new HallazgoRiesgo("lluvia", TipoAlerta.CLIMA, NivelRiesgo.ALTO, d.fecha(),
                        "Lluvia intensa " + cuando,
                        String.format(Locale.US, "Se estiman %.0f mm de lluvia %s.", d.lluviaMm(), cuando),
                        "Revisa los drenajes y evita fertilizar o fumigar antes de la lluvia."));
            } else if (d.lluviaMm() >= LLUVIA_MEDIA_MM && d.probLluvia() >= 80) {
                r.add(new HallazgoRiesgo("lluvia", TipoAlerta.CLIMA, NivelRiesgo.MEDIO, d.fecha(),
                        "Lluvia fuerte " + cuando,
                        String.format(Locale.US, "Probabilidad de lluvia de %d %% (%.0f mm) %s.", d.probLluvia(), d.lluviaMm(), cuando),
                        "Programa las labores al aire libre para otro día."));
            }
            if (d.vientoKmh() >= VIENTO_FUERTE_KMH) {
                r.add(new HallazgoRiesgo("viento", TipoAlerta.CLIMA, NivelRiesgo.MEDIO, d.fecha(),
                        "Vientos fuertes " + cuando,
                        String.format(Locale.US, "Se esperan vientos de hasta %.0f km/h %s.", d.vientoKmh(), cuando),
                        "Asegura tutores, plásticos y estructuras livianas."));
            }
        }
        if (dias.size() >= 5 && dias.subList(0, 5).stream().allMatch(d -> d.probLluvia() <= 15)) {
            r.add(new HallazgoRiesgo("sequia", TipoAlerta.CLIMA, NivelRiesgo.MEDIO, dias.get(0).fecha(),
                    "Varios días sin lluvia",
                    "Se esperan al menos 5 días con muy baja probabilidad de lluvia.",
                    "Planifica el riego y cubre el suelo con paja o hojarasca para conservar humedad."));
        }
        return r;
    }
}
