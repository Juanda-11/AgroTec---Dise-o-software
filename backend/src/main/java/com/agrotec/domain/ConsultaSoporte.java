package com.agrotec.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Consulta técnica del agricultor a un asesor. RF-13. */
@Document("consultas_soporte")
public class ConsultaSoporte {

    @Id
    private String id;
    private String usuarioId;
    private String mensaje;
    /** URLs de las fotos adjuntas (máximo 3). */
    private List<String> fotos = new ArrayList<>();
    private EstadoConsulta estado = EstadoConsulta.ENVIADA;
    private String respuesta;
    private String asesorNombre;
    private Instant creadaEn = Instant.now();
    private Instant respondidaEn;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public List<String> getFotos() { return fotos; }
    public void setFotos(List<String> fotos) { this.fotos = fotos; }
    public EstadoConsulta getEstado() { return estado; }
    public void setEstado(EstadoConsulta estado) { this.estado = estado; }
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
    public String getAsesorNombre() { return asesorNombre; }
    public void setAsesorNombre(String asesorNombre) { this.asesorNombre = asesorNombre; }
    public Instant getCreadaEn() { return creadaEn; }
    public void setCreadaEn(Instant creadaEn) { this.creadaEn = creadaEn; }
    public Instant getRespondidaEn() { return respondidaEn; }
    public void setRespondidaEn(Instant respondidaEn) { this.respondidaEn = respondidaEn; }
}
