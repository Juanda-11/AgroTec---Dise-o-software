package com.agrotec.service;

import com.agrotec.domain.Alerta;
import com.agrotec.domain.Cultivo;
import com.agrotec.domain.EstadoCultivo;
import com.agrotec.domain.Finca;
import com.agrotec.domain.NivelRiesgo;
import com.agrotec.domain.TipoAlerta;
import com.agrotec.domain.Usuario;
import com.agrotec.dto.AlertaDto;
import com.agrotec.event.AlertaCreadaEvent;
import com.agrotec.exception.NotFoundException;
import com.agrotec.integration.weather.Forecast;
import com.agrotec.repository.AlertaRepository;
import com.agrotec.repository.CultivoRepository;
import com.agrotec.repository.FincaRepository;
import com.agrotec.repository.UsuarioRepository;
import com.agrotec.service.rules.HallazgoRiesgo;
import com.agrotec.service.rules.ReglasClimaticas;
import com.agrotec.service.rules.ReglasPlagas;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/** Genera y consulta alertas climáticas, de plagas y de otros tipos (RF-06, RF-07). */
@Service
public class AlertaService {

    private static final Logger log = LoggerFactory.getLogger(AlertaService.class);

    private final AlertaRepository alertas;
    private final FincaRepository fincas;
    private final CultivoRepository cultivos;
    private final UsuarioRepository usuarios;
    private final ClimaService clima;
    private final ApplicationEventPublisher eventos;

    public AlertaService(AlertaRepository alertas, FincaRepository fincas, CultivoRepository cultivos,
                         UsuarioRepository usuarios, ClimaService clima, ApplicationEventPublisher eventos) {
        this.alertas = alertas;
        this.fincas = fincas;
        this.cultivos = cultivos;
        this.usuarios = usuarios;
        this.clima = clima;
        this.eventos = eventos;
    }

    public List<AlertaDto.Response> listar(String usuarioId, TipoAlerta tipo) {
        List<Alerta> lista = tipo == null
                ? alertas.findByUsuarioIdOrderByCreadaEnDesc(usuarioId)
                : alertas.findByUsuarioIdAndTipoOrderByCreadaEnDesc(usuarioId, tipo);
        return lista.stream().map(AlertaDto.Response::de).toList();
    }

    public AlertaDto.Response marcarLeida(String usuarioId, String id) {
        Alerta a = alertas.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new NotFoundException("Alerta no encontrada"));
        a.setLeida(true);
        return AlertaDto.Response.de(alertas.save(a));
    }

    /** Evalúa clima y plagas de un agricultor y crea las alertas nuevas. Devuelve cuántas creó. */
    public int evaluar(Usuario u) {
        Optional<Finca> finca = fincas.findByUsuarioId(u.getId());
        if (finca.isEmpty()) {
            return 0;
        }
        Forecast fc = clima.forecast(finca.get());
        String fuente = "AgroTec con pronóstico de " + fc.fuente();
        int nuevas = 0;

        for (HallazgoRiesgo h : ReglasClimaticas.evaluar(fc.dias())) {
            if (crear(u.getId(), h.tipo(), h.nivel(), h.titulo(), h.mensaje(), h.recomendacion(), null, fuente,
                    h.regla() + ":" + h.fechaEvento()).isPresent()) {
                nuevas++;
            }
        }
        List<Cultivo> activos = cultivos.findByUsuarioIdOrderByActualizadoEnDesc(u.getId()).stream()
                .filter(c -> c.getEstado() == EstadoCultivo.ACTIVO).toList();
        for (Cultivo c : activos) {
            List<HallazgoRiesgo> plagas = new ArrayList<>(ReglasPlagas.evaluar(c.getTipo(), fc.dias()));
            for (HallazgoRiesgo h : plagas) {
                String titulo = h.titulo() + " (" + c.getTipo() + " · " + c.getLote() + ")";
                if (crear(u.getId(), h.tipo(), h.nivel(), titulo, h.mensaje(), h.recomendacion(), c.getId(), fuente,
                        h.regla() + ":" + h.fechaEvento() + ":" + c.getId()).isPresent()) {
                    nuevas++;
                }
            }
        }
        return nuevas;
    }

    /** Crea una alerta si no existe otra con la misma clave (evita duplicados) y avisa a los observadores. */
    public Optional<Alerta> crear(String usuarioId, TipoAlerta tipo, NivelRiesgo nivel, String titulo, String mensaje,
                                  String recomendacion, String cultivoId, String fuente, String clave) {
        if (alertas.existsByUsuarioIdAndClave(usuarioId, clave)) {
            return Optional.empty();
        }
        Alerta a = new Alerta();
        a.setUsuarioId(usuarioId);
        a.setTipo(tipo);
        a.setNivel(nivel);
        a.setTitulo(titulo);
        a.setMensaje(mensaje);
        a.setRecomendacion(recomendacion);
        a.setCultivoId(cultivoId);
        a.setFuente(fuente);
        a.setClave(clave);
        Alerta guardada = alertas.save(a);
        eventos.publishEvent(new AlertaCreadaEvent(guardada));
        return Optional.of(guardada);
    }

    /** Tarea programada: revisa a todos los agricultores con finca. Un error en uno no detiene a los demás. */
    public void evaluarTodos() {
        for (Finca f : fincas.findAll()) {
            try {
                usuarios.findById(f.getUsuarioId()).ifPresent(this::evaluar);
            } catch (Exception e) {
                log.warn("No se pudieron evaluar alertas del usuario {}: {}", f.getUsuarioId(), e.getMessage());
            }
        }
    }
}
