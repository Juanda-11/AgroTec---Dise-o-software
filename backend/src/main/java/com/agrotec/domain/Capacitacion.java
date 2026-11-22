package com.agrotec.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Módulo de capacitación (video o guía). RF-12. */
@Document("capacitaciones")
public class Capacitacion {

    @Id
    private String id;
    private String titulo;
    private String descripcion;
    /** Cultivo al que aplica; nulo si es general (p. ej. "Cómo usar AgroTec"). */
    private String cultivo;
    private FormatoCapacitacion formato;
    private int duracionMin;
    private String nivel;
    private String entidad;
    private String url;
    private boolean descargable = true;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCultivo() { return cultivo; }
    public void setCultivo(String cultivo) { this.cultivo = cultivo; }
    public FormatoCapacitacion getFormato() { return formato; }
    public void setFormato(FormatoCapacitacion formato) { this.formato = formato; }
    public int getDuracionMin() { return duracionMin; }
    public void setDuracionMin(int duracionMin) { this.duracionMin = duracionMin; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public String getEntidad() { return entidad; }
    public void setEntidad(String entidad) { this.entidad = entidad; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public boolean isDescargable() { return descargable; }
    public void setDescargable(boolean descargable) { this.descargable = descargable; }
}
