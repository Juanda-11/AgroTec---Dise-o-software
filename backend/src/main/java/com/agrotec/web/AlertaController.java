package com.agrotec.web;

import com.agrotec.domain.TipoAlerta;
import com.agrotec.dto.AlertaDto;
import com.agrotec.security.AuthUser;
import com.agrotec.service.AlertaService;
import com.agrotec.service.PerfilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alertas")
@Tag(name = "Alertas", description = "RF-06 y RF-07: alertas de clima, plagas, precio y labores")
public class AlertaController {

    private final AlertaService alertas;
    private final PerfilService perfil;

    public AlertaController(AlertaService alertas, PerfilService perfil) {
        this.alertas = alertas;
        this.perfil = perfil;
    }

    @GetMapping
    public List<AlertaDto.Response> listar(@AuthenticationPrincipal AuthUser user,
                                           @RequestParam(required = false) TipoAlerta tipo) {
        return alertas.listar(user.id(), tipo);
    }

    @PatchMapping("/{id}/leida")
    public AlertaDto.Response marcarLeida(@AuthenticationPrincipal AuthUser user, @PathVariable String id) {
        return alertas.marcarLeida(user.id(), id);
    }

    /** Fuerza la revisión de clima y plagas del usuario (útil en demostraciones). */
    @PostMapping("/evaluar")
    public Map<String, Integer> evaluar(@AuthenticationPrincipal AuthUser user) {
        return Map.of("nuevas", alertas.evaluar(perfil.usuario(user.id())));
    }
}
