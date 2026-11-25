package com.agrotec.service;

import com.agrotec.domain.Cultivo;
import com.agrotec.domain.EstadoCultivo;
import com.agrotec.dto.CultivoDto;
import com.agrotec.exception.NotFoundException;
import com.agrotec.repository.ActividadRepository;
import com.agrotec.repository.CultivoRepository;
import com.agrotec.util.Textos;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/** CRUD de cultivos del agricultor (RF-03). Todas las consultas filtran por el dueño. */
@Service
public class CultivoService {

    private final CultivoRepository cultivos;
    private final ActividadRepository actividades;

    public CultivoService(CultivoRepository cultivos, ActividadRepository actividades) {
        this.cultivos = cultivos;
        this.actividades = actividades;
    }

    public List<CultivoDto.Response> listar(String usuarioId, String q, EstadoCultivo estado) {
        String buscado = Textos.normalizar(q);
        return cultivos.findByUsuarioIdOrderByActualizadoEnDesc(usuarioId).stream()
                .filter(c -> estado == null || c.getEstado() == estado)
                .filter(c -> buscado.isEmpty()
                        || Textos.normalizar(c.getTipo()).contains(buscado)
                        || Textos.normalizar(c.getLote()).contains(buscado)
                        || Textos.normalizar(c.getVariedad()).contains(buscado))
                .map(CultivoDto.Response::de)
                .toList();
    }

    public CultivoDto.Response crear(String usuarioId, CultivoDto.Request r) {
        // Idempotencia: si el celular reintenta un alta hecha sin conexión, no se duplica.
        if (r.clienteId() != null && !r.clienteId().isBlank()) {
            var existente = cultivos.findByUsuarioIdAndClienteId(usuarioId, r.clienteId());
            if (existente.isPresent()) {
                return CultivoDto.Response.de(existente.get());
            }
        }
        Cultivo c = new Cultivo();
        c.setUsuarioId(usuarioId);
        c.setClienteId(r.clienteId());
        aplicar(c, r);
        return CultivoDto.Response.de(cultivos.save(c));
    }

    public CultivoDto.Response obtener(String usuarioId, String id) {
        return CultivoDto.Response.de(buscar(usuarioId, id));
    }

    public CultivoDto.Response actualizar(String usuarioId, String id, CultivoDto.Request r) {
        Cultivo c = buscar(usuarioId, id);
        aplicar(c, r);
        return CultivoDto.Response.de(cultivos.save(c));
    }

    public CultivoDto.Response cambiarEstado(String usuarioId, String id, EstadoCultivo estado) {
        Cultivo c = buscar(usuarioId, id);
        c.cambiarEstado(estado);
        return CultivoDto.Response.de(cultivos.save(c));
    }

    public void eliminar(String usuarioId, String id) {
        Cultivo c = buscar(usuarioId, id);
        actividades.deleteByCultivoId(c.getId());
        cultivos.delete(c);
    }

    /** Uso interno de otros servicios: verifica que el cultivo exista y sea del usuario. */
    public Cultivo buscar(String usuarioId, String id) {
        return cultivos.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new NotFoundException("Cultivo no encontrado"));
    }

    private void aplicar(Cultivo c, CultivoDto.Request r) {
        c.setTipo(r.tipo().trim());
        c.setLote(r.lote().trim());
        c.setVariedad(r.variedad());
        c.setAreaHa(r.areaHa());
        c.setFechaSiembra(r.fechaSiembra());
        c.setFechaCosechaEstimada(r.fechaCosechaEstimada());
        c.setNotas(r.notas());
        c.setActualizadoEn(Instant.now());
    }
}
