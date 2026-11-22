package com.agrotec.web;

import com.agrotec.dto.ClimaDto;
import com.agrotec.security.AuthUser;
import com.agrotec.service.ClimaService;
import com.agrotec.service.PerfilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clima")
@Tag(name = "Clima", description = "RF-05: pronóstico de la finca (3 días Gratis, 7 días Plus)")
public class ClimaController {

    private final ClimaService clima;
    private final PerfilService perfil;

    public ClimaController(ClimaService clima, PerfilService perfil) {
        this.clima = clima;
        this.perfil = perfil;
    }

    @GetMapping
    public ClimaDto.Response pronostico(@AuthenticationPrincipal AuthUser user) {
        return clima.pronostico(perfil.usuario(user.id()));
    }
}
