package com.agrotec.domain;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/** Alerta climática, de plaga, de precio o de labor dirigida a un agricultor. RF-06, RF-07. */
@Document("alertas")
@CompoundIndex(name = "usuario_clave", def = "{'usuarioId': 1, 'clave': 1}")
public class Alerta {

    @Id
    private String id;
    private String usuarioId;
    private TipoAlerta tipo;
    private NivelRiesgo nivel;
    private String titulo;
    private String mensaje;
    private String recomendacion;
    private String cultivoId;
    /** RNF-06: toda alerta indica de dónde proviene la información. */
    private String fuente;
    /** Clave de deduplicación (misma regla + mismo día + mismo cultivo = una sola alerta). */
    private String clave;
    private Instant creadaEn = Instant.now();
    private boolean leida;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public TipoAlerta getTipo() { return tipo; }
    public void setTipo(TipoAlerta tipo) { this.tipo = tipo; }
    public NivelRiesgo getNivel() { return nivel; }
    public void setNivel(NivelRiesgo nivel) { this.nivel = nivel; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getRecomendacion() { return recomendacion; }
    public void setRecomendacion(String recomendacion) { this.recomendacion = recomendacion; }
    public String getCultivoId() { return cultivoId; }
    public void setCultivoId(String cultivoId) { this.cultivoId = cultivoId; }
    public String getFuente() { return fuente; }
    public void setFuente(String fuente) { this.fuente = fuente; }
    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public Instant getCreadaEn() { return creadaEn; }
    public void setCreadaEn(Instant creadaEn) { this.creadaEn = creadaEn; }
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
}
