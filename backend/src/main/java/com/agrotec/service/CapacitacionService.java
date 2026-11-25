package com.agrotec.service;

import com.agrotec.domain.AvanceCapacitacion;
import com.agrotec.domain.Capacitacion;
import com.agrotec.dto.CapacitacionDto;
import com.agrotec.exception.NotFoundException;
import com.agrotec.repository.AvanceCapacitacionRepository;
import com.agrotec.repository.CapacitacionRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** Catálogo de capacitaciones y avance por agricultor (RF-12). */
@Service
public class CapacitacionService {

    private final CapacitacionRepository capacitaciones;
    private final AvanceCapacitacionRepository avances;

    public CapacitacionService(CapacitacionRepository capacitaciones, AvanceCapacitacionRepository avances) {
        this.capacitaciones = capacitaciones;
        this.avances = avances;
    }

    /** @param cultivo filtra por cultivo; los módulos generales (sin cultivo) siempre se incluyen. */
    public List<CapacitacionDto.Response> listar(String usuarioId, String cultivo) {
        Map<String, Integer> avancePorModulo = new HashMap<>();
        avances.findByUsuarioId(usuarioId).forEach(a -> avancePorModulo.put(a.getCapacitacionId(), a.getPorcentaje()));
        return capacitaciones.findAll().stream()
                .filter(c -> c.aplicaA(cultivo))
                .map(c -> aRespuesta(c, avancePorModulo.getOrDefault(c.getId(), 0)))
                .toList();
    }

    public CapacitacionDto.Response registrarAvance(String usuarioId, String capacitacionId, int porcentaje) {
        Capacitacion c = capacitaciones.findById(capacitacionId)
                .orElseThrow(() -> new NotFoundException("Capacitación no encontrada"));
        AvanceCapacitacion a = avances.findByUsuarioIdAndCapacitacionId(usuarioId, capacitacionId)
                .orElseGet(AvanceCapacitacion::new);
        a.setUsuarioId(usuarioId);
        a.setCapacitacionId(capacitacionId);
        a.actualizar(porcentaje);
        avances.save(a);
        return aRespuesta(c, porcentaje);
    }

    private CapacitacionDto.Response aRespuesta(Capacitacion c, int avance) {
        return new CapacitacionDto.Response(c.getId(), c.getTitulo(), c.getDescripcion(), c.getCultivo(), c.getFormato(),
                c.getDuracionMin(), c.getNivel(), c.getEntidad(), c.getUrl(), c.isDescargable(), avance);
    }
}
