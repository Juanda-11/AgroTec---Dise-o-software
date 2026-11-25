package com.agrotec.service;

import com.agrotec.domain.Actividad;
import com.agrotec.dto.ActividadDto;
import com.agrotec.exception.NotFoundException;
import com.agrotec.repository.ActividadRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/** Historial de labores y recordatorios de cada cultivo (RF-04). */
@Service
public class ActividadService {

    private final ActividadRepository actividades;
    private final CultivoService cultivos;

    public ActividadService(ActividadRepository actividades, CultivoService cultivos) {
        this.actividades = actividades;
        this.cultivos = cultivos;
    }

    public List<ActividadDto.Response> listar(String usuarioId, String cultivoId) {
        cultivos.buscar(usuarioId, cultivoId);
        return actividades.findByCultivoIdAndUsuarioIdOrderByFechaDesc(cultivoId, usuarioId).stream()
                .map(ActividadDto.Response::de).toList();
    }

    public ActividadDto.Response crear(String usuarioId, String cultivoId, ActividadDto.Request r) {
        cultivos.buscar(usuarioId, cultivoId);
        if (r.clienteId() != null && !r.clienteId().isBlank()) {
            var existente = actividades.findByUsuarioIdAndClienteId(usuarioId, r.clienteId());
            if (existente.isPresent()) {
                return ActividadDto.Response.de(existente.get());
            }
        }
        Actividad a = new Actividad();
        a.setUsuarioId(usuarioId);
        a.setCultivoId(cultivoId);
        a.setClienteId(r.clienteId());
        aplicar(a, r);
        return ActividadDto.Response.de(actividades.save(a));
    }

    public ActividadDto.Response actualizar(String usuarioId, String id, ActividadDto.Request r) {
        Actividad a = buscar(usuarioId, id);
        aplicar(a, r);
        a.setRecordatorioEnviado(false);
        return ActividadDto.Response.de(actividades.save(a));
    }

    public ActividadDto.Response completar(String usuarioId, String id) {
        Actividad a = buscar(usuarioId, id);
        a.completar();
        return ActividadDto.Response.de(actividades.save(a));
    }

    public void eliminar(String usuarioId, String id) {
        actividades.delete(buscar(usuarioId, id));
    }

    /** Labores con recordatorio que aún no se han completado, la más próxima primero. */
    public List<ActividadDto.Response> pendientes(String usuarioId) {
        return actividades.findByUsuarioIdAndCompletadaFalseAndRecordatorioNotNullOrderByRecordatorioAsc(usuarioId)
                .stream().map(ActividadDto.Response::de).toList();
    }

    private Actividad buscar(String usuarioId, String id) {
        return actividades.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new NotFoundException("Actividad no encontrada"));
    }

    private void aplicar(Actividad a, ActividadDto.Request r) {
        a.setTipo(r.tipo());
        a.setFecha(r.fecha());
        a.setNota(r.nota());
        a.setRecordatorio(r.recordatorio());
        a.setActualizadoEn(Instant.now());
    }
}
