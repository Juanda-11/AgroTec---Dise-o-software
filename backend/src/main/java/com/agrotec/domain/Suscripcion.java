package com.agrotec.domain;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Registro de una suscripción al plan Plus. RF-14. */
@Document("suscripciones")
public class Suscripcion {

    @Id
    private String id;
    private String usuarioId;
    private PlanTipo plan;
    private boolean cancelada;
    private Instant inicio;
    private Instant fin;
    private MetodoPago metodoPago;
    private String referenciaPago;
    private long monto;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public PlanTipo getPlan() { return plan; }
    public void setPlan(PlanTipo plan) { this.plan = plan; }
    public boolean isCancelada() { return cancelada; }
    public void setCancelada(boolean cancelada) { this.cancelada = cancelada; }
    public Instant getInicio() { return inicio; }
    public void setInicio(Instant inicio) { this.inicio = inicio; }
    public Instant getFin() { return fin; }
    public void setFin(Instant fin) { this.fin = fin; }
    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
    public String getReferenciaPago() { return referenciaPago; }
    public void setReferenciaPago(String referenciaPago) { this.referenciaPago = referenciaPago; }
    public long getMonto() { return monto; }
    public void setMonto(long monto) { this.monto = monto; }
}
