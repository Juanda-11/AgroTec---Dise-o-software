package com.agrotec.service;

import com.agrotec.domain.PlanTipo;
import com.agrotec.domain.Suscripcion;
import com.agrotec.domain.Usuario;
import com.agrotec.dto.SuscripcionDto;
import com.agrotec.exception.BusinessException;
import com.agrotec.integration.payment.PaymentGateway;
import com.agrotec.integration.payment.ResultadoPago;
import com.agrotec.integration.payment.SolicitudPago;
import com.agrotec.repository.SuscripcionRepository;
import com.agrotec.repository.UsuarioRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/** Modelo freemium: plan Gratis y plan Plus mensual (RF-14). El cobro se delega a una pasarela (Adapter). */
@Service
public class SuscripcionService {

    public static final long PRECIO_PLUS = 30_000L;
    private static final Duration PERIODO = Duration.ofDays(30);

    private final SuscripcionRepository suscripciones;
    private final UsuarioRepository usuarios;
    private final PaymentGateway pasarela;
    private final NotificacionService notificaciones;

    public SuscripcionService(SuscripcionRepository suscripciones, UsuarioRepository usuarios, PaymentGateway pasarela,
                              NotificacionService notificaciones) {
        this.suscripciones = suscripciones;
        this.usuarios = usuarios;
        this.pasarela = pasarela;
        this.notificaciones = notificaciones;
    }

    public List<SuscripcionDto.PlanInfo> planes() {
        return List.of(
                new SuscripcionDto.PlanInfo(PlanTipo.GRATIS, "Plan Gratis", 0, List.of(
                        "Registro básico de cultivos", "Pronóstico a 3 días", "Precios del día", "Capacitaciones")),
                new SuscripcionDto.PlanInfo(PlanTipo.PLUS, "Plan AgroTec Plus", PRECIO_PLUS, List.of(
                        "Todo lo del plan Gratis", "Pronóstico a 7 días", "Alertas por WhatsApp y SMS",
                        "Tendencias de precio y alertas de precio objetivo", "Asesor técnico")));
    }

    public SuscripcionDto.Estado estado(Usuario u) {
        var ultima = suscripciones.findFirstByUsuarioIdAndCanceladaFalseOrderByFinDesc(u.getId());
        boolean cancelada = ultima.isEmpty() && u.getPlanVigenteHasta() != null && u.planEfectivo() == PlanTipo.PLUS;
        return new SuscripcionDto.Estado(u.planEfectivo(), u.getPlanVigenteHasta(), cancelada, null);
    }

    public SuscripcionDto.Estado suscribir(Usuario u, SuscripcionDto.SuscribirRequest r) {
        if (r.plan() != PlanTipo.PLUS) {
            throw new BusinessException("El plan Gratis no requiere pago");
        }
        ResultadoPago pago = pasarela.cobrar(new SolicitudPago(u.getId(), PRECIO_PLUS, r.metodoPago(), r.tokenPago()));
        if (!pago.aprobado()) {
            throw new BusinessException(pago.mensaje());
        }
        Instant ahora = Instant.now();
        // Si ya tiene Plus vigente, se extiende desde su fecha de vencimiento.
        Instant base = u.planEfectivo() == PlanTipo.PLUS ? u.getPlanVigenteHasta() : ahora;
        Instant fin = base.plus(PERIODO);

        Suscripcion s = new Suscripcion();
        s.setUsuarioId(u.getId());
        s.setPlan(PlanTipo.PLUS);
        s.setInicio(ahora);
        s.setFin(fin);
        s.setMetodoPago(r.metodoPago());
        s.setReferenciaPago(pago.referencia());
        s.setMonto(PRECIO_PLUS);
        suscripciones.save(s);

        u.setPlan(PlanTipo.PLUS);
        u.setPlanVigenteHasta(fin);
        usuarios.save(u);
        notificaciones.enviarMensaje(u, null, "Pago confirmado",
                "Tu plan Plus está activo hasta el " + fin.toString().substring(0, 10) + ". Ref. " + pago.referencia());
        return new SuscripcionDto.Estado(PlanTipo.PLUS, fin, false, "Pago aprobado. ¡Gracias por suscribirte!");
    }

    /** Cancela la renovación; el agricultor conserva el plan Plus hasta que termine el período pagado. */
    public SuscripcionDto.Estado cancelar(Usuario u) {
        var activa = suscripciones.findFirstByUsuarioIdAndCanceladaFalseOrderByFinDesc(u.getId())
                .orElseThrow(() -> new BusinessException("No tienes una suscripción activa para cancelar"));
        suscripciones.findByUsuarioIdOrderByInicioDesc(u.getId()).forEach(s -> {
            if (!s.isCancelada()) {
                s.setCancelada(true);
                suscripciones.save(s);
            }
        });
        return new SuscripcionDto.Estado(u.planEfectivo(), activa.getFin(), true,
                "Suscripción cancelada. Seguirás con Plus hasta el final del período pagado.");
    }
}
