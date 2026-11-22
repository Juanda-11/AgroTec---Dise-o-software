package com.agrotec.service;

import com.agrotec.domain.Actividad;
import com.agrotec.domain.NivelRiesgo;
import com.agrotec.domain.TipoAlerta;
import com.agrotec.repository.ActividadRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/** Convierte los recordatorios vencidos en alertas de tipo LABOR (HU recordatorios de labores). */
@Service
public class RecordatorioService {

    private final ActividadRepository actividades;
    private final AlertaService alertas;

    public RecordatorioService(ActividadRepository actividades, AlertaService alertas) {
        this.actividades = actividades;
        this.alertas = alertas;
    }

    public int procesarVencidos() {
        List<Actividad> vencidas = actividades
                .findByRecordatorioLessThanEqualAndCompletadaFalseAndRecordatorioEnviadoFalse(Instant.now());
        for (Actividad a : vencidas) {
            String nombre = a.getTipo().name().toLowerCase().replace('_', ' ');
            alertas.crear(a.getUsuarioId(), TipoAlerta.LABOR, NivelRiesgo.BAJO,
                    "Recordatorio: " + nombre,
                    a.getNota() != null && !a.getNota().isBlank() ? a.getNota() : "Tienes una labor programada.",
                    "Cuando la termines, márcala como hecha en la app.", a.getCultivoId(),
                    "Recordatorio programado por ti", "recordatorio:" + a.getId());
            a.setRecordatorioEnviado(true);
            actividades.save(a);
        }
        return vencidas.size();
    }
}
