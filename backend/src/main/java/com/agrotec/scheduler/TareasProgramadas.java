package com.agrotec.scheduler;

import com.agrotec.service.AlertaService;
import com.agrotec.service.PrecioService;
import com.agrotec.service.RecordatorioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Tareas periódicas del sistema: recordatorios, alertas climáticas/plagas y alertas de precio. */
@Component
public class TareasProgramadas {

    private static final Logger log = LoggerFactory.getLogger(TareasProgramadas.class);

    private final AlertaService alertas;
    private final RecordatorioService recordatorios;
    private final PrecioService precios;

    public TareasProgramadas(AlertaService alertas, RecordatorioService recordatorios, PrecioService precios) {
        this.alertas = alertas;
        this.recordatorios = recordatorios;
        this.precios = precios;
    }

    /** Cada minuto: convierte recordatorios vencidos en alertas. */
    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void recordatorios() {
        try {
            int n = recordatorios.procesarVencidos();
            if (n > 0) {
                log.info("Recordatorios enviados: {}", n);
            }
        } catch (Exception e) {
            log.warn("Error procesando recordatorios: {}", e.getMessage());
        }
    }

    /** Cada hora: revisa el pronóstico de todas las fincas y genera alertas. */
    @Scheduled(cron = "0 0 * * * *", zone = "America/Bogota")
    public void alertasClimaYPlagas() {
        alertas.evaluarTodos();
    }

    /** Todos los días a las 6:30 a. m. (hora de Colombia): alertas de precio objetivo. */
    @Scheduled(cron = "0 30 6 * * *", zone = "America/Bogota")
    public void alertasDePrecio() {
        try {
            precios.evaluarAlertasPrecio();
        } catch (Exception e) {
            log.warn("Error evaluando alertas de precio: {}", e.getMessage());
        }
    }
}
