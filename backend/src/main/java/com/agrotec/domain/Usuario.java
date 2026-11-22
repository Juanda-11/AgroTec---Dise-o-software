package com.agrotec.domain;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** Agricultor (o asesor/administrador) registrado en AgroTec. RF-01, RF-02. */
@Document("usuarios")
public class Usuario {

    @Id
    private String id;
    @Indexed(unique = true)
    private String celular;
    private String nombre;
    private String passwordHash;
    private Rol rol = Rol.AGRICULTOR;

    /** RNF-05: consentimiento informado de tratamiento de datos (Ley 1581 de 2012). */
    private boolean consentimientoDatos;
    private Instant consentimientoFecha;

    private PreferenciasNotificacion preferencias = new PreferenciasNotificacion();

    private PlanTipo plan = PlanTipo.GRATIS;
    private Instant planVigenteHasta;

    private String codigoRecuperacionHash;
    private Instant codigoRecuperacionExpira;
    private int intentosFallidos;
    private Instant bloqueadoHasta;

    private Instant creadoEn = Instant.now();

    /** Plan realmente activo: PLUS solo mientras la suscripción esté vigente. */
    public PlanTipo planEfectivo() {
        if (plan == PlanTipo.PLUS && planVigenteHasta != null && planVigenteHasta.isAfter(Instant.now())) {
            return PlanTipo.PLUS;
        }
        return PlanTipo.GRATIS;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public boolean isConsentimientoDatos() { return consentimientoDatos; }
    public void setConsentimientoDatos(boolean consentimientoDatos) { this.consentimientoDatos = consentimientoDatos; }
    public Instant getConsentimientoFecha() { return consentimientoFecha; }
    public void setConsentimientoFecha(Instant consentimientoFecha) { this.consentimientoFecha = consentimientoFecha; }
    public PreferenciasNotificacion getPreferencias() { return preferencias; }
    public void setPreferencias(PreferenciasNotificacion preferencias) { this.preferencias = preferencias; }
    public PlanTipo getPlan() { return plan; }
    public void setPlan(PlanTipo plan) { this.plan = plan; }
    public Instant getPlanVigenteHasta() { return planVigenteHasta; }
    public void setPlanVigenteHasta(Instant planVigenteHasta) { this.planVigenteHasta = planVigenteHasta; }
    public String getCodigoRecuperacionHash() { return codigoRecuperacionHash; }
    public void setCodigoRecuperacionHash(String codigoRecuperacionHash) { this.codigoRecuperacionHash = codigoRecuperacionHash; }
    public Instant getCodigoRecuperacionExpira() { return codigoRecuperacionExpira; }
    public void setCodigoRecuperacionExpira(Instant codigoRecuperacionExpira) { this.codigoRecuperacionExpira = codigoRecuperacionExpira; }
    public int getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }
    public Instant getBloqueadoHasta() { return bloqueadoHasta; }
    public void setBloqueadoHasta(Instant bloqueadoHasta) { this.bloqueadoHasta = bloqueadoHasta; }
    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
}
