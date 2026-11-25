package com.agrotec.domain;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/** Cultivo registrado por el agricultor. RF-03. */
@Document("cultivos")
@CompoundIndex(name = "usuario_cliente", def = "{'usuarioId': 1, 'clienteId': 1}")
public class Cultivo {

    @Id
    private String id;
    private String usuarioId;
    /** Identificador generado en el celular cuando se crea sin conexión (RF-10). */
    private String clienteId;
    private String tipo;
    private String lote;
    private String variedad;
    private Double areaHa;
    private LocalDate fechaSiembra;
    private LocalDate fechaCosechaEstimada;
    private EstadoCultivo estado = EstadoCultivo.ACTIVO;
    private String notas;
    private Instant actualizadoEn = Instant.now();


    public void cambiarEstado(EstadoCultivo nuevo) {
        estado = nuevo;
        actualizadoEn = Instant.now();
    }

    public boolean estaActivo() {
        return estado == EstadoCultivo.ACTIVO;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }
    public String getVariedad() { return variedad; }
    public void setVariedad(String variedad) { this.variedad = variedad; }
    public Double getAreaHa() { return areaHa; }
    public void setAreaHa(Double areaHa) { this.areaHa = areaHa; }
    public LocalDate getFechaSiembra() { return fechaSiembra; }
    public void setFechaSiembra(LocalDate fechaSiembra) { this.fechaSiembra = fechaSiembra; }
    public LocalDate getFechaCosechaEstimada() { return fechaCosechaEstimada; }
    public void setFechaCosechaEstimada(LocalDate fechaCosechaEstimada) { this.fechaCosechaEstimada = fechaCosechaEstimada; }
    public EstadoCultivo getEstado() { return estado; }
    public void setEstado(EstadoCultivo estado) { this.estado = estado; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
