package com.agrotec.domain;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/** Porcentaje de avance de un agricultor en un módulo de capacitación. */
@Document("avances_capacitacion")
@CompoundIndex(name = "usuario_capacitacion", def = "{'usuarioId': 1, 'capacitacionId': 1}", unique = true)
public class AvanceCapacitacion {

    @Id
    private String id;
    private String usuarioId;
    private String capacitacionId;
    private int porcentaje;
    private Instant actualizadoEn = Instant.now();


    public void actualizar(int nuevoPorcentaje) {
        porcentaje = nuevoPorcentaje;
        actualizadoEn = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getCapacitacionId() { return capacitacionId; }
    public void setCapacitacionId(String capacitacionId) { this.capacitacionId = capacitacionId; }
    public int getPorcentaje() { return porcentaje; }
    public void setPorcentaje(int porcentaje) { this.porcentaje = porcentaje; }
    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
