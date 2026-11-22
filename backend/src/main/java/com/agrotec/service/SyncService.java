package com.agrotec.service;

import com.agrotec.domain.Actividad;
import com.agrotec.domain.Cultivo;
import com.agrotec.domain.EstadoCultivo;
import com.agrotec.dto.ActividadDto;
import com.agrotec.dto.AlertaDto;
import com.agrotec.dto.CultivoDto;
import com.agrotec.dto.SyncDto;
import com.agrotec.repository.ActividadRepository;
import com.agrotec.repository.AlertaRepository;
import com.agrotec.repository.CultivoRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Sincronización con el celular (RF-10). El celular guarda cambios sin conexión y los envía en lote;
 * el servidor resuelve conflictos con "gana el más reciente" y es idempotente gracias al {@code clienteId}.
 */
@Service
public class SyncService {

    private final CultivoRepository cultivos;
    private final ActividadRepository actividades;
    private final AlertaRepository alertas;

    public SyncService(CultivoRepository cultivos, ActividadRepository actividades, AlertaRepository alertas) {
        this.cultivos = cultivos;
        this.actividades = actividades;
        this.alertas = alertas;
    }

    public SyncDto.Response sincronizar(String usuarioId, SyncDto.Request req) {
        Instant ahora = Instant.now();
        List<SyncDto.Resultado> resultados = new ArrayList<>();
        Map<String, String> idPorClienteCultivo = new HashMap<>();

        for (SyncDto.Cultivo sc : lista(req.cultivos())) {
            Instant t = SyncMerge.limitar(sc.actualizadoEn(), ahora);
            Optional<Cultivo> existente = cultivos.findByUsuarioIdAndClienteId(usuarioId, sc.clienteId());
            if (existente.isEmpty()) {
                Cultivo c = new Cultivo();
                c.setUsuarioId(usuarioId);
                c.setClienteId(sc.clienteId());
                aplicar(c, sc, t);
                c = cultivos.save(c);
                idPorClienteCultivo.put(sc.clienteId(), c.getId());
                resultados.add(new SyncDto.Resultado("cultivo", sc.clienteId(), c.getId(), "CREADO", null));
            } else {
                Cultivo c = existente.get();
                idPorClienteCultivo.put(sc.clienteId(), c.getId());
                if (SyncMerge.clienteGana(t, c.getActualizadoEn())) {
                    aplicar(c, sc, t);
                    cultivos.save(c);
                    resultados.add(new SyncDto.Resultado("cultivo", sc.clienteId(), c.getId(), "ACTUALIZADO", null));
                } else {
                    resultados.add(new SyncDto.Resultado("cultivo", sc.clienteId(), c.getId(), "IGNORADO",
                            "El servidor tiene un cambio más reciente"));
                }
            }
        }

        for (SyncDto.Actividad sa : lista(req.actividades())) {
            String cultivoId = resolverCultivo(usuarioId, sa, idPorClienteCultivo);
            if (cultivoId == null) {
                resultados.add(new SyncDto.Resultado("actividad", sa.clienteId(), null, "ERROR",
                        "No se encontró el cultivo de la actividad"));
                continue;
            }
            Instant t = SyncMerge.limitar(sa.actualizadoEn(), ahora);
            Optional<Actividad> existente = actividades.findByUsuarioIdAndClienteId(usuarioId, sa.clienteId());
            if (existente.isEmpty()) {
                Actividad a = new Actividad();
                a.setUsuarioId(usuarioId);
                a.setClienteId(sa.clienteId());
                a.setCultivoId(cultivoId);
                aplicar(a, sa, t);
                a = actividades.save(a);
                resultados.add(new SyncDto.Resultado("actividad", sa.clienteId(), a.getId(), "CREADO", null));
            } else {
                Actividad a = existente.get();
                if (SyncMerge.clienteGana(t, a.getActualizadoEn())) {
                    aplicar(a, sa, t);
                    a.setCultivoId(cultivoId);
                    actividades.save(a);
                    resultados.add(new SyncDto.Resultado("actividad", sa.clienteId(), a.getId(), "ACTUALIZADO", null));
                } else {
                    resultados.add(new SyncDto.Resultado("actividad", sa.clienteId(), a.getId(), "IGNORADO",
                            "El servidor tiene un cambio más reciente"));
                }
            }
        }
        return new SyncDto.Response(resultados, Instant.now());
    }

    /** Descarga incremental: solo lo que cambió desde la última sincronización (ahorra datos, RNF-02). */
    public SyncDto.Cambios cambios(String usuarioId, Instant desde) {
        Instant d = desde == null ? Instant.EPOCH : desde;
        return new SyncDto.Cambios(
                cultivos.findByUsuarioIdAndActualizadoEnAfter(usuarioId, d).stream().map(CultivoDto.Response::de).toList(),
                actividades.findByUsuarioIdAndActualizadoEnAfter(usuarioId, d).stream().map(ActividadDto.Response::de).toList(),
                alertas.findByUsuarioIdAndCreadaEnAfter(usuarioId, d).stream().map(AlertaDto.Response::de).toList(),
                Instant.now());
    }

    private String resolverCultivo(String usuarioId, SyncDto.Actividad sa, Map<String, String> idPorCliente) {
        if (sa.cultivoId() != null && !sa.cultivoId().isBlank()) {
            return cultivos.findByIdAndUsuarioId(sa.cultivoId(), usuarioId).map(Cultivo::getId).orElse(null);
        }
        if (sa.cultivoClienteId() != null) {
            String id = idPorCliente.get(sa.cultivoClienteId());
            if (id != null) {
                return id;
            }
            return cultivos.findByUsuarioIdAndClienteId(usuarioId, sa.cultivoClienteId()).map(Cultivo::getId).orElse(null);
        }
        return null;
    }

    private void aplicar(Cultivo c, SyncDto.Cultivo s, Instant t) {
        c.setTipo(s.tipo());
        c.setLote(s.lote());
        c.setVariedad(s.variedad());
        c.setAreaHa(s.areaHa());
        c.setFechaSiembra(s.fechaSiembra());
        c.setFechaCosechaEstimada(s.fechaCosechaEstimada());
        c.setEstado(s.estado() == null ? EstadoCultivo.ACTIVO : s.estado());
        c.setNotas(s.notas());
        c.setActualizadoEn(t);
    }

    private void aplicar(Actividad a, SyncDto.Actividad s, Instant t) {
        a.setTipo(s.tipo());
        a.setFecha(s.fecha());
        a.setNota(s.nota());
        a.setRecordatorio(s.recordatorio());
        a.setCompletada(s.completada());
        a.setActualizadoEn(t);
    }

    private static <T> List<T> lista(List<T> l) {
        return l == null ? List.of() : l;
    }
}
