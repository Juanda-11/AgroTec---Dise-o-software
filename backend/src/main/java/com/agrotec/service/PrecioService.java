package com.agrotec.service;

import com.agrotec.domain.AlertaPrecio;
import com.agrotec.domain.CategoriaPrecio;
import com.agrotec.domain.NivelRiesgo;
import com.agrotec.domain.PlanTipo;
import com.agrotec.domain.PrecioMercado;
import com.agrotec.domain.Tendencia;
import com.agrotec.domain.TipoAlerta;
import com.agrotec.domain.Usuario;
import com.agrotec.dto.PrecioDto;
import com.agrotec.exception.NotFoundException;
import com.agrotec.repository.AlertaPrecioRepository;
import com.agrotec.repository.PrecioRepository;
import com.agrotec.util.Textos;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Consulta de precios de insumos y productos, tendencias y alertas de precio objetivo (RF-08, RF-09). */
@Service
public class PrecioService {

    private static final Logger log = LoggerFactory.getLogger(PrecioService.class);

    public static final List<String> PLAZAS = List.of("Pasto", "Ipiales", "Tumaco");
    public static final String PLAZA_POR_DEFECTO = "Pasto";
    private static final int DIAS_TENDENCIA_GRATIS = 7;
    private static final int DIAS_TENDENCIA_PLUS = 90;

    private final PrecioRepository precios;
    private final AlertaPrecioRepository alertasPrecio;
    private final AlertaService alertas;
    private final PlanGuard planGuard;

    public PrecioService(PrecioRepository precios, AlertaPrecioRepository alertasPrecio, AlertaService alertas,
                         PlanGuard planGuard) {
        this.precios = precios;
        this.alertasPrecio = alertasPrecio;
        this.alertas = alertas;
        this.planGuard = planGuard;
    }

    public List<PrecioDto.Item> listar(CategoriaPrecio categoria, String plaza, String q) {
        Optional<LocalDate> ultima = ultimaFecha();
        if (ultima.isEmpty()) {
            return List.of();
        }
        String buscado = Textos.normalizar(q);
        return precios.findByCategoriaAndPlazaAndFecha(categoria, plazaValida(plaza), ultima.get()).stream()
                .filter(p -> buscado.isEmpty() || Textos.normalizar(p.getProducto()).contains(buscado))
                .map(this::item)
                .toList();
    }

    /** Precio de referencia para el panel de inicio: el producto más importante de la zona (café). */
    public PrecioDto.Item referencia(String plaza) {
        Optional<LocalDate> ultima = ultimaFecha();
        if (ultima.isEmpty()) {
            return null;
        }
        return precios.findByCategoriaAndPlazaAndFecha(CategoriaPrecio.PRODUCTO, plazaValida(plaza), ultima.get())
                .stream().findFirst().map(this::item).orElse(null);
    }

    public PrecioDto.TendenciaResponse tendencia(Usuario u, String producto, String plaza, int dias) {
        LocalDate ultima = ultimaFecha().orElseThrow(() -> new NotFoundException("Aún no hay precios cargados"));
        int maximo = u.planEfectivo() == PlanTipo.PLUS ? DIAS_TENDENCIA_PLUS : DIAS_TENDENCIA_GRATIS;
        int d = Math.max(2, Math.min(dias, maximo));
        String plazaOk = plazaValida(plaza);
        List<PrecioMercado> serie = precios.findByProductoIgnoreCaseAndPlazaAndFechaGreaterThanEqualOrderByFechaAsc(
                producto, plazaOk, ultima.minusDays(d - 1L));
        if (serie.isEmpty()) {
            throw new NotFoundException("No hay precios para ese producto en " + plazaOk);
        }
        PrecioMercado ref = serie.get(serie.size() - 1);
        return new PrecioDto.TendenciaResponse(ref.getProducto(), plazaOk, ref.getUnidad(),
                serie.stream().map(p -> new PrecioDto.Punto(p.getFecha(), p.getPrecio())).toList(), ref.getFuente());
    }

    /** Carga/actualiza un precio (lo usa el administrador; simula la integración con SIPSA-DANE). */
    public PrecioDto.Item ingestar(PrecioDto.IngestaRequest r) {
        PrecioMercado p = precios.findByProductoIgnoreCaseAndPlazaAndFecha(r.producto(), r.plaza(), r.fecha())
                .orElseGet(PrecioMercado::new);
        p.setProducto(r.producto());
        p.setCategoria(r.categoria());
        p.setPlaza(r.plaza());
        p.setUnidad(r.unidad());
        p.setPrecio(r.precio());
        p.setFecha(r.fecha());
        p.setFuente(r.fuente());
        PrecioMercado guardado = precios.save(p);
        evaluarAlertasPrecio();
        return item(guardado);
    }

    // ----- alertas de precio objetivo -----

    public List<PrecioDto.AlertaResponse> misAlertas(String usuarioId) {
        return alertasPrecio.findByUsuarioId(usuarioId).stream().map(PrecioDto.AlertaResponse::de).toList();
    }

    public PrecioDto.AlertaResponse crearAlerta(Usuario u, PrecioDto.AlertaRequest r) {
        planGuard.requierePlus(u, "Alerta de precio objetivo");
        String plaza = plazaValida(r.plaza());
        LocalDate ultima = ultimaFecha().orElseThrow(() -> new NotFoundException("Aún no hay precios cargados"));
        precios.findByProductoIgnoreCaseAndPlazaAndFecha(r.producto(), plaza, ultima)
                .orElseThrow(() -> new NotFoundException("No existe ese producto en " + plaza));
        AlertaPrecio a = new AlertaPrecio();
        a.setUsuarioId(u.getId());
        a.setProducto(r.producto());
        a.setPlaza(plaza);
        a.setPrecioObjetivo(r.precioObjetivo());
        return PrecioDto.AlertaResponse.de(alertasPrecio.save(a));
    }

    public void eliminarAlerta(String usuarioId, String id) {
        AlertaPrecio a = alertasPrecio.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new NotFoundException("Alerta de precio no encontrada"));
        alertasPrecio.delete(a);
    }

    /** Si un precio alcanzó el objetivo del agricultor, crea una alerta (una por día). */
    public int evaluarAlertasPrecio() {
        Optional<LocalDate> ultima = ultimaFecha();
        if (ultima.isEmpty()) {
            return 0;
        }
        int enviadas = 0;
        for (AlertaPrecio ap : alertasPrecio.findByActivaTrue()) {
            try {
                var actual = precios.findByProductoIgnoreCaseAndPlazaAndFecha(ap.getProducto(), ap.getPlaza(), ultima.get());
                if (actual.isPresent() && actual.get().getPrecio() >= ap.getPrecioObjetivo()
                        && !ultima.get().equals(ap.getUltimaNotificacion())) {
                    PrecioMercado p = actual.get();
                    alertas.crear(ap.getUsuarioId(), TipoAlerta.PRECIO, NivelRiesgo.BAJO,
                            p.getProducto() + " alcanzó tu precio objetivo",
                            String.format("En %s está a $%,d por %s (tu objetivo: $%,d).", p.getPlaza(), p.getPrecio(),
                                    p.getUnidad(), ap.getPrecioObjetivo()),
                            "Es un buen momento para comparar compradores y decidir si vendes.", null, p.getFuente(),
                            "precio:" + ap.getId() + ":" + ultima.get());
                    ap.setUltimaNotificacion(ultima.get());
                    alertasPrecio.save(ap);
                    enviadas++;
                }
            } catch (Exception e) {
                log.warn("No se pudo evaluar la alerta de precio {}: {}", ap.getId(), e.getMessage());
            }
        }
        return enviadas;
    }

    // ----- utilidades -----

    private Optional<LocalDate> ultimaFecha() {
        return precios.findTopByOrderByFechaDesc().map(PrecioMercado::getFecha);
    }

    private String plazaValida(String plaza) {
        return PLAZAS.stream().filter(p -> p.equalsIgnoreCase(plaza)).findFirst().orElse(PLAZA_POR_DEFECTO);
    }

    private PrecioDto.Item item(PrecioMercado p) {
        double variacion = precios.findByProductoIgnoreCaseAndPlazaAndFecha(p.getProducto(), p.getPlaza(),
                        p.getFecha().minusDays(1))
                .filter(prev -> prev.getPrecio() > 0)
                .map(prev -> (p.getPrecio() - prev.getPrecio()) * 100.0 / prev.getPrecio())
                .orElse(0.0);
        double redondeada = Math.round(variacion * 10) / 10.0;
        Tendencia t = Math.abs(redondeada) < 0.5 ? Tendencia.ESTABLE : (redondeada > 0 ? Tendencia.SUBE : Tendencia.BAJA);
        boolean viejo = p.getFecha().isBefore(LocalDate.now(Textos.ZONA).minusDays(1));
        return new PrecioDto.Item(p.getProducto(), p.getCategoria(), p.getPlaza(), p.getUnidad(), p.getPrecio(),
                redondeada, t, p.getFecha(), p.getFuente(), viejo);
    }
}
