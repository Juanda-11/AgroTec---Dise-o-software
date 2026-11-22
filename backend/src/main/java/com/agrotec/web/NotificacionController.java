package com.agrotec.web;

import com.agrotec.dto.PreferenciasDto;
import com.agrotec.integration.notification.MensajeEnviado;
import com.agrotec.security.AuthUser;
import com.agrotec.service.NotificacionService;
import com.agrotec.service.PerfilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "RF-11: canales, tipos de alerta y horario silencioso")
public class NotificacionController {

    private final NotificacionService notificaciones;
    private final PerfilService perfil;

    public NotificacionController(NotificacionService notificaciones, PerfilService perfil) {
        this.notificaciones = notificaciones;
        this.perfil = perfil;
    }

    @GetMapping("/preferencias")
    public PreferenciasDto.Response preferencias(@AuthenticationPrincipal AuthUser user) {
        return notificaciones.preferencias(perfil.usuario(user.id()));
    }

    @PutMapping("/preferencias")
    public PreferenciasDto.Response actualizar(@AuthenticationPrincipal AuthUser user,
                                               @Valid @RequestBody PreferenciasDto.Request r) {
        return notificaciones.actualizar(perfil.usuario(user.id()), r);
    }

    /** Últimos mensajes "enviados" (push, WhatsApp y SMS simulados) para poder verlos en la demostración. */
    @GetMapping("/bandeja")
    public List<MensajeEnviado> bandeja(@AuthenticationPrincipal AuthUser user) {
        return notificaciones.bandeja(user.id());
    }
}
