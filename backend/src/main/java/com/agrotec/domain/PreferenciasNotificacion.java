package com.agrotec.domain;

import com.agrotec.util.HorarioSilencioso;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

/** Preferencias de notificación del agricultor (documento embebido en Usuario). RF-11. */
public class PreferenciasNotificacion {

    private Set<Canal> canales = new LinkedHashSet<>(Set.of(Canal.PUSH));
    private Set<TipoAlerta> tipos = new LinkedHashSet<>(Set.of(TipoAlerta.CLIMA, TipoAlerta.PLAGA, TipoAlerta.LABOR));
    /** Número confirmado para WhatsApp/SMS. Si es nulo se usa el celular de la cuenta. */
    private String numeroWhatsapp;
    /** Formato HH:mm. Ejemplo: 21:00 a 05:00. */
    private String silencioInicio;
    private String silencioFin;

    /** ¿El agricultor quiere recibir este tipo de alerta? */
    public boolean permite(TipoAlerta tipo) {
        return tipos.contains(tipo);
    }

    /** ¿La hora cae dentro del horario silencioso? */
    public boolean estaEnSilencio(LocalTime hora) {
        return HorarioSilencioso.estaEnSilencio(hora, silencioInicio, silencioFin);
    }

    public Set<Canal> getCanales() { return canales; }
    public void setCanales(Set<Canal> canales) { this.canales = canales; }
    public Set<TipoAlerta> getTipos() { return tipos; }
    public void setTipos(Set<TipoAlerta> tipos) { this.tipos = tipos; }
    public String getNumeroWhatsapp() { return numeroWhatsapp; }
    public void setNumeroWhatsapp(String numeroWhatsapp) { this.numeroWhatsapp = numeroWhatsapp; }
    public String getSilencioInicio() { return silencioInicio; }
    public void setSilencioInicio(String silencioInicio) { this.silencioInicio = silencioInicio; }
    public String getSilencioFin() { return silencioFin; }
    public void setSilencioFin(String silencioFin) { this.silencioFin = silencioFin; }
}
