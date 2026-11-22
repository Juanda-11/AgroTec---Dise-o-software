package com.agrotec.web;

import com.agrotec.dto.SuscripcionDto;
import com.agrotec.security.AuthUser;
import com.agrotec.service.PerfilService;
import com.agrotec.service.SuscripcionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Planes y suscripción", description = "RF-14: modelo freemium")
public class SuscripcionController {

    private final SuscripcionService suscripciones;
    private final PerfilService perfil;

    public SuscripcionController(SuscripcionService suscripciones, PerfilService perfil) {
        this.suscripciones = suscripciones;
        this.perfil = perfil;
    }

    @GetMapping("/planes")
    public List<SuscripcionDto.PlanInfo> planes() {
        return suscripciones.planes();
    }

    @GetMapping("/suscripcion")
    public SuscripcionDto.Estado estado(@AuthenticationPrincipal AuthUser user) {
        return suscripciones.estado(perfil.usuario(user.id()));
    }

    @PostMapping("/suscripcion")
    public SuscripcionDto.Estado suscribir(@AuthenticationPrincipal AuthUser user,
                                           @Valid @RequestBody SuscripcionDto.SuscribirRequest r) {
        return suscripciones.suscribir(perfil.usuario(user.id()), r);
    }

    @DeleteMapping("/suscripcion")
    public SuscripcionDto.Estado cancelar(@AuthenticationPrincipal AuthUser user) {
        return suscripciones.cancelar(perfil.usuario(user.id()));
    }
}
