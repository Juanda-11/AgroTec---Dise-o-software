package com.agrotec.service;

import com.agrotec.domain.ConsultaSoporte;
import com.agrotec.domain.EstadoConsulta;
import com.agrotec.domain.Usuario;
import com.agrotec.dto.SoporteDto;
import com.agrotec.exception.NotFoundException;
import com.agrotec.repository.ConsultaSoporteRepository;
import com.agrotec.repository.UsuarioRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/** Consultas del agricultor a asesores técnicos (RF-13). Enviar consultas requiere plan Plus. */
@Service
public class SoporteService {

    private final ConsultaSoporteRepository consultas;
    private final UsuarioRepository usuarios;
    private final PlanGuard planGuard;
    private final NotificacionService notificaciones;

    public SoporteService(ConsultaSoporteRepository consultas, UsuarioRepository usuarios, PlanGuard planGuard,
                          NotificacionService notificaciones) {
        this.consultas = consultas;
        this.usuarios = usuarios;
        this.planGuard = planGuard;
        this.notificaciones = notificaciones;
    }

    public SoporteDto.Response crear(Usuario u, SoporteDto.Request r) {
        planGuard.requierePlus(u, "Asesor técnico");
        ConsultaSoporte c = new ConsultaSoporte();
        c.setUsuarioId(u.getId());
        c.setMensaje(r.mensaje().trim());
        c.setFotos(r.fotos() == null ? new ArrayList<>() : new ArrayList<>(r.fotos()));
        return SoporteDto.Response.de(consultas.save(c));
    }

    public List<SoporteDto.Response> mias(String usuarioId) {
        return consultas.findByUsuarioIdOrderByCreadaEnDesc(usuarioId).stream().map(SoporteDto.Response::de).toList();
    }

    public SoporteDto.Response obtener(String usuarioId, String id) {
        return SoporteDto.Response.de(consultas.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new NotFoundException("Consulta no encontrada")));
    }

    // ----- lado del asesor -----

    public List<SoporteDto.Response> pendientes(EstadoConsulta estado) {
        EstadoConsulta e = estado == null ? EstadoConsulta.ENVIADA : estado;
        return consultas.findByEstadoOrderByCreadaEnAsc(e).stream().map(SoporteDto.Response::de).toList();
    }

    public SoporteDto.Response tomar(String id) {
        ConsultaSoporte c = buscar(id);
        if (c.getEstado() == EstadoConsulta.ENVIADA) {
            c.setEstado(EstadoConsulta.EN_REVISION);
        }
        return SoporteDto.Response.de(consultas.save(c));
    }

    public SoporteDto.Response responder(String asesorId, String id, SoporteDto.RespuestaRequest r) {
        ConsultaSoporte c = buscar(id);
        Usuario asesor = usuarios.findById(asesorId).orElseThrow(() -> new NotFoundException("Asesor no encontrado"));
        c.setRespuesta(r.respuesta().trim());
        c.setEstado(EstadoConsulta.RESPONDIDA);
        c.setAsesorNombre(asesor.getNombre());
        c.setRespondidaEn(Instant.now());
        ConsultaSoporte guardada = consultas.save(c);
        usuarios.findById(c.getUsuarioId()).ifPresent(agricultor ->
                notificaciones.enviarMensaje(agricultor, null, "Tu asesor respondió tu consulta", guardada.getRespuesta()));
        return SoporteDto.Response.de(guardada);
    }

    private ConsultaSoporte buscar(String id) {
        return consultas.findById(id).orElseThrow(() -> new NotFoundException("Consulta no encontrada"));
    }
}
