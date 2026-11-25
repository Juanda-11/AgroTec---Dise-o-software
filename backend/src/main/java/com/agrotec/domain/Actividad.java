package com.agrotec.domain;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Labor realizada (o programada) en un cultivo. RF-04. */
@Document("actividades")
public class Actividad {

    @Id
    private String id;
    private String usuarioId;
    private String cultivoId;
    private String clienteId;
    private TipoActividad tipo;
    private LocalDate fecha;
    private String nota;
    /** Momento en que debe sonar el recordatorio (opcional). */
    private Instant recordatorio;
    private boolean recordatorioEnviado;
    private boolean completada;
    private Instant actualizadoEn = Instant.now();


    public void completar() {
        completada = true;
        actualizadoEn = Instant.now();
    }

    public void marcarRecordatorioEnviado() {
        recordatorioEnviado = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getCultivoId() { return cultivoId; }
    public void setCultivoId(String cultivoId) { this.cultivoId = cultivoId; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public TipoActividad getTipo() { return tipo; }
    public void setTipo(TipoActividad tipo) { this.tipo = tipo; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }
    public Instant getRecordatorio() { return recordatorio; }
    public void setRecordatorio(Instant recordatorio) { this.recordatorio = recordatorio; }
    public boolean isRecordatorioEnviado() { return recordatorioEnviado; }
    public void setRecordatorioEnviado(boolean recordatorioEnviado) { this.recordatorioEnviado = recordatorioEnviado; }
    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
