package com.agrotec.config;

import com.agrotec.domain.Actividad;
import com.agrotec.domain.Capacitacion;
import com.agrotec.domain.CategoriaPrecio;
import com.agrotec.domain.Cultivo;
import com.agrotec.domain.Finca;
import com.agrotec.domain.FormatoCapacitacion;
import com.agrotec.domain.PlanTipo;
import com.agrotec.domain.PrecioMercado;
import com.agrotec.domain.Rol;
import com.agrotec.domain.TipoActividad;
import com.agrotec.domain.Usuario;
import com.agrotec.repository.ActividadRepository;
import com.agrotec.repository.CapacitacionRepository;
import com.agrotec.repository.CultivoRepository;
import com.agrotec.repository.FincaRepository;
import com.agrotec.repository.PrecioRepository;
import com.agrotec.repository.UsuarioRepository;
import com.agrotec.service.PrecioService;
import com.agrotec.util.Textos;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Carga datos de ejemplo al iniciar: capacitaciones, 30 días de precios y usuarios de demostración.
 * LOS PRECIOS SON SIMULADOS (no son datos reales de SIPSA-DANE) y los usuarios de demostración son solo
 * para desarrollo: se desactivan con SEED_DEMO_USERS=false.
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final String FUENTE = "Datos de ejemplo (simulan SIPSA-DANE)";

    private record Producto(String nombre, CategoriaPrecio categoria, String unidad, long base) {
    }

    private static final List<Producto> PRODUCTOS = List.of(
            new Producto("Café pergamino", CategoriaPrecio.PRODUCTO, "carga 125 kg", 2_150_000),
            new Producto("Papa pastusa", CategoriaPrecio.PRODUCTO, "bulto 50 kg", 85_000),
            new Producto("Maíz amarillo", CategoriaPrecio.PRODUCTO, "bulto 50 kg", 98_000),
            new Producto("Fríjol cargamanto", CategoriaPrecio.PRODUCTO, "kilo", 7_800),
            new Producto("Arveja verde", CategoriaPrecio.PRODUCTO, "kilo", 4_200),
            new Producto("Urea 46 %", CategoriaPrecio.INSUMO, "bulto 50 kg", 165_000),
            new Producto("DAP 18-46-0", CategoriaPrecio.INSUMO, "bulto 50 kg", 210_000),
            new Producto("Abono orgánico compostado", CategoriaPrecio.INSUMO, "bulto 50 kg", 38_000),
            new Producto("Semilla de papa pastusa", CategoriaPrecio.INSUMO, "bulto 50 kg", 120_000),
            new Producto("Cal dolomita", CategoriaPrecio.INSUMO, "bulto 50 kg", 22_000));

    private final PrecioRepository precios;
    private final CapacitacionRepository capacitaciones;
    private final UsuarioRepository usuarios;
    private final FincaRepository fincas;
    private final CultivoRepository cultivos;
    private final ActividadRepository actividades;
    private final PasswordEncoder encoder;
    private final boolean habilitado;
    private final boolean usuariosDemo;

    public DataSeeder(PrecioRepository precios, CapacitacionRepository capacitaciones, UsuarioRepository usuarios,
                      FincaRepository fincas, CultivoRepository cultivos, ActividadRepository actividades,
                      PasswordEncoder encoder,
                      @Value("${agrotec.seed.enabled:true}") boolean habilitado,
                      @Value("${agrotec.seed.demo-users:false}") boolean usuariosDemo) {
        this.precios = precios;
        this.capacitaciones = capacitaciones;
        this.usuarios = usuarios;
        this.fincas = fincas;
        this.cultivos = cultivos;
        this.actividades = actividades;
        this.encoder = encoder;
        this.habilitado = habilitado;
        this.usuariosDemo = usuariosDemo;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!habilitado) {
            return;
        }
        cargarCapacitaciones();
        cargarPrecios();
        if (usuariosDemo) {
            cargarUsuariosDemo();
        }
    }

    private void cargarCapacitaciones() {
        if (capacitaciones.count() > 0) {
            return;
        }
        capacitaciones.saveAll(List.of(
                cap("Cómo usar AgroTec", "Recorrido por la app en 5 minutos.", null, FormatoCapacitacion.VIDEO, 5, "Básico"),
                cap("Leer el pronóstico del clima", "Aprende a interpretar temperatura, lluvia y alertas.", null,
                        FormatoCapacitacion.VIDEO, 8, "Básico"),
                cap("Registro de labores y cosechas", "Cómo llevar el historial de tu cultivo.", null,
                        FormatoCapacitacion.VIDEO, 6, "Básico"),
                cap("Control de la broca del café", "Reconocer y manejar la broca.", "Café", FormatoCapacitacion.VIDEO, 12, "Básico"),
                cap("Manejo de la roya del café", "Prevención y control de la roya.", "Café", FormatoCapacitacion.VIDEO, 10, "Intermedio"),
                cap("Fertilización de la papa", "Guía de fertilización por etapa.", "Papa", FormatoCapacitacion.GUIA_PDF, 15, "Intermedio"),
                cap("Siembra y manejo del maíz", "Guía práctica de siembra.", "Maíz", FormatoCapacitacion.GUIA_PDF, 12, "Básico")));
        log.info("Capacitaciones de ejemplo cargadas");
    }

    private Capacitacion cap(String titulo, String desc, String cultivo, FormatoCapacitacion formato, int min, String nivel) {
        Capacitacion c = new Capacitacion();
        c.setTitulo(titulo);
        c.setDescripcion(desc);
        c.setCultivo(cultivo);
        c.setFormato(formato);
        c.setDuracionMin(min);
        c.setNivel(nivel);
        c.setEntidad("Contenido de ejemplo");
        c.setUrl("#");
        return c;
    }

    /** Genera (o completa hasta hoy) la serie diaria de precios con un recorrido aleatorio determinista. */
    private void cargarPrecios() {
        LocalDate hoy = LocalDate.now(Textos.ZONA);
        LocalDate ultima = precios.findTopByOrderByFechaDesc().map(PrecioMercado::getFecha).orElse(null);
        if (ultima != null && !ultima.isBefore(hoy)) {
            return;
        }
        LocalDate desde = ultima == null ? hoy.minusDays(29) : ultima.plusDays(1);
        List<PrecioMercado> nuevos = new ArrayList<>();
        double[] factorPlaza = {1.00, 0.97, 1.04};
        for (Producto prod : PRODUCTOS) {
            for (int i = 0; i < PrecioService.PLAZAS.size(); i++) {
                String plaza = PrecioService.PLAZAS.get(i);
                final LocalDate fechaAnterior = ultima;
                double precio = fechaAnterior == null
                        ? prod.base() * factorPlaza[i] * 0.97
                        : precios.findByProductoIgnoreCaseAndPlazaAndFecha(prod.nombre(), plaza, fechaAnterior)
                                .map(p -> (double) p.getPrecio()).orElse(prod.base() * factorPlaza[i]);
                Random r = new Random((prod.nombre() + plaza).hashCode() + desde.toEpochDay());
                for (LocalDate f = desde; !f.isAfter(hoy); f = f.plusDays(1)) {
                    precio = precio * (1 + (r.nextDouble() - 0.45) * 0.04);
                    PrecioMercado p = new PrecioMercado();
                    p.setProducto(prod.nombre());
                    p.setCategoria(prod.categoria());
                    p.setPlaza(plaza);
                    p.setUnidad(prod.unidad());
                    p.setPrecio(Math.round(precio / 50.0) * 50);
                    p.setFecha(f);
                    p.setFuente(FUENTE);
                    nuevos.add(p);
                }
            }
        }
        precios.saveAll(nuevos);
        log.info("Precios de ejemplo cargados: {} registros ({} a {})", nuevos.size(), desde, hoy);
    }

    private void cargarUsuariosDemo() {
        crearUsuario("Administrador", "3000000000", "admin123", Rol.ADMIN);
        crearUsuario("Ing. Laura Martínez (asesora)", "3000000001", "asesor123", Rol.ASESOR);
        if (usuarios.existsByCelular("3001234567")) {
            return;
        }
        Usuario d = crearUsuario("David Mateo Torres", "3001234567", "1234", Rol.AGRICULTOR);
        d.setPlan(PlanTipo.PLUS);
        d.setPlanVigenteHasta(Instant.now().plus(30, ChronoUnit.DAYS));
        usuarios.save(d);

        Finca f = new Finca();
        f.setUsuarioId(d.getId());
        f.setNombre("Finca La Esperanza");
        f.setDepartamento("Nariño");
        f.setMunicipio("Pasto");
        f.setVereda("Genoy");
        fincas.save(f);

        Cultivo cafe = cultivo(d.getId(), "Café", "Lote 1", "Castillo", 0.5, LocalDate.now().minusMonths(8));
        Cultivo papa = cultivo(d.getId(), "Papa", "Lote 2", "Pastusa Suprema", 0.3, LocalDate.now().minusMonths(2));
        cultivos.saveAll(List.of(cafe, papa));

        Actividad a = new Actividad();
        a.setUsuarioId(d.getId());
        a.setCultivoId(cafe.getId());
        a.setTipo(TipoActividad.FERTILIZACION);
        a.setFecha(LocalDate.now().minusDays(10));
        a.setNota("Fertilización con 15 g por planta");
        actividades.save(a);
        log.warn("Usuarios de DEMOSTRACIÓN creados (solo desarrollo): 3001234567/1234 (agricultor Plus), "
                + "3000000001/asesor123 (asesor), 3000000000/admin123 (admin)");
    }

    private Usuario crearUsuario(String nombre, String celular, String clave, Rol rol) {
        return usuarios.findByCelular(celular).orElseGet(() -> {
            Usuario u = new Usuario();
            u.setNombre(nombre);
            u.setCelular(celular);
            u.setPasswordHash(encoder.encode(clave));
            u.setRol(rol);
            u.setConsentimientoDatos(true);
            u.setConsentimientoFecha(Instant.now());
            return usuarios.save(u);
        });
    }

    private Cultivo cultivo(String usuarioId, String tipo, String lote, String variedad, double area, LocalDate siembra) {
        Cultivo c = new Cultivo();
        c.setUsuarioId(usuarioId);
        c.setTipo(tipo);
        c.setLote(lote);
        c.setVariedad(variedad);
        c.setAreaHa(area);
        c.setFechaSiembra(siembra);
        return c;
    }
}
