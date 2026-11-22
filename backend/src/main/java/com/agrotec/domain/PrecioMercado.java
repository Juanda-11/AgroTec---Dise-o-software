package com.agrotec.domain;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/** Precio diario de un producto o insumo en una plaza. RF-08, RF-09. Valores en pesos colombianos (COP). */
@Document("precios")
@CompoundIndex(name = "producto_plaza_fecha", def = "{'producto': 1, 'plaza': 1, 'fecha': -1}")
public class PrecioMercado {

    @Id
    private String id;
    private String producto;
    private CategoriaPrecio categoria;
    private String plaza;
    private String unidad;
    private long precio;
    private LocalDate fecha;
    private String fuente;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public CategoriaPrecio getCategoria() { return categoria; }
    public void setCategoria(CategoriaPrecio categoria) { this.categoria = categoria; }
    public String getPlaza() { return plaza; }
    public void setPlaza(String plaza) { this.plaza = plaza; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public long getPrecio() { return precio; }
    public void setPrecio(long precio) { this.precio = precio; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getFuente() { return fuente; }
    public void setFuente(String fuente) { this.fuente = fuente; }
}
