package com.agrotec.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/** Comportamiento de las entidades del dominio (los métodos que muestra la vista lógica). */
class DominioTest {

    private final Instant ahora = Instant.parse("2026-11-24T12:00:00Z");

    @Test
    void laCuentaSeBloqueaAlQuintoIntentoFallido() {
        Usuario u = new Usuario();
        for (int i = 0; i < 4; i++) {
            u.registrarIntentoFallido(ahora, 5, Duration.ofMinutes(15));
        }
        assertFalse(u.estaBloqueado(ahora));
        u.registrarIntentoFallido(ahora, 5, Duration.ofMinutes(15));
        assertTrue(u.estaBloqueado(ahora));
        assertFalse(u.estaBloqueado(ahora.plusSeconds(16 * 60)));
        u.reiniciarIntentos();
        assertFalse(u.estaBloqueado(ahora));
    }

    @Test
    void activarPlusCambiaElPlanEfectivo() {
        Usuario u = new Usuario();
        assertEquals(PlanTipo.GRATIS, u.planEfectivo());
        u.activarPlus(Instant.now().plusSeconds(3600));
        assertEquals(PlanTipo.PLUS, u.planEfectivo());
    }

    @Test
    void laAlertaDePrecioAvisaUnaSolaVezPorDia() {
        AlertaPrecio a = new AlertaPrecio();
        a.setPrecioObjetivo(100);
        LocalDate hoy = LocalDate.of(2026, 11, 24);
        assertFalse(a.seCumple(99, hoy));
        assertTrue(a.seCumple(100, hoy));
        a.marcarNotificada(hoy);
        assertFalse(a.seCumple(120, hoy));
        assertTrue(a.seCumple(120, hoy.plusDays(1)));
    }

    @Test
    void variacionDePrecio() {
        PrecioMercado p = new PrecioMercado();
        p.setPrecio(110);
        assertEquals(10.0, p.variacionRespecto(100), 0.0001);
        assertEquals(0.0, p.variacionRespecto(0), 0.0001);
    }

    @Test
    void cicloDeVidaDeUnaConsulta() {
        ConsultaSoporte c = new ConsultaSoporte();
        c.tomar();
        assertEquals(EstadoConsulta.EN_REVISION, c.getEstado());
        c.responder("Aplica abono", "Laura");
        assertEquals(EstadoConsulta.RESPONDIDA, c.getEstado());
        assertEquals("Laura", c.getAsesorNombre());
    }

    @Test
    void capacitacionGeneralAplicaACualquierCultivo() {
        Capacitacion general = new Capacitacion();
        Capacitacion cafe = new Capacitacion();
        cafe.setCultivo("Café");
        assertTrue(general.aplicaA("Papa"));
        assertTrue(cafe.aplicaA("cafe"));
        assertFalse(cafe.aplicaA("Papa"));
    }

    @Test
    void cultivoCosechadoDejaDeEstarActivo() {
        Cultivo c = new Cultivo();
        assertTrue(c.estaActivo());
        c.cambiarEstado(EstadoCultivo.COSECHADO);
        assertFalse(c.estaActivo());
    }
}
