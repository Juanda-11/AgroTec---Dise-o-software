package com.agrotec.web;

import com.agrotec.dto.InicioDto;
import com.agrotec.security.AuthUser;
import com.agrotec.service.InicioService;
import com.agrotec.service.PerfilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inicio")
@Tag(name = "Inicio", description = "RF-15: resumen del día en una sola llamada (ahorra datos)")
public class InicioController {

    private final InicioService inicio;
    private final PerfilService perfil;

    public InicioController(InicioService inicio, PerfilService perfil) {
        this.inicio = inicio;
        this.perfil = perfil;
    }

    @GetMapping
    public InicioDto.Response resumen(@AuthenticationPrincipal AuthUser user) {
        return inicio.resumen(perfil.usuario(user.id()));
    }
}
