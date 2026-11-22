package com.agrotec.domain;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Alerta de precio objetivo configurada por el agricultor. RF-09 (HU precio objetivo). */
@Document("alertas_precio")
public class AlertaPrecio {

    @Id
    private String id;
    private String usuarioId;
    private String producto;
    private String plaza;
    private long precioObjetivo;
    private boolean activa = true;
    /** Último día en que se notificó (evita avisar varias veces el mismo día). */
    private LocalDate ultimaNotificacion;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public String getPlaza() { return plaza; }
    public void setPlaza(String plaza) { this.plaza = plaza; }
    public long getPrecioObjetivo() { return precioObjetivo; }
    public void setPrecioObjetivo(long precioObjetivo) { this.precioObjetivo = precioObjetivo; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public LocalDate getUltimaNotificacion() { return ultimaNotificacion; }
    public void setUltimaNotificacion(LocalDate ultimaNotificacion) { this.ultimaNotificacion = ultimaNotificacion; }
}
