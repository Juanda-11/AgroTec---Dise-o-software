package com.agrotec.domain;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** Finca del agricultor; su ubicación personaliza clima, alertas y precios. RF-02. */
@Document("fincas")
public class Finca {

    @Id
    private String id;
    @Indexed(unique = true)
    private String usuarioId;
    private String nombre;
    private String departamento;
    private String municipio;
    private String vereda;
    private Double latitud;
    private Double longitud;
    private Instant actualizadoEn = Instant.now();


    /** true si tiene coordenadas propias (GPS); si no, se usa la zona del municipio. */
    public boolean tieneUbicacion() {
        return latitud != null && longitud != null;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }
    public String getVereda() { return vereda; }
    public void setVereda(String vereda) { this.vereda = vereda; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
