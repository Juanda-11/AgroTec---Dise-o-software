package com.agrotec.web;

import com.agrotec.domain.EstadoConsulta;
import com.agrotec.dto.SoporteDto;
import com.agrotec.security.AuthUser;
import com.agrotec.service.PerfilService;
import com.agrotec.service.SoporteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Soporte técnico", description = "RF-13: consultas del agricultor (Plus) y respuestas del asesor")
public class SoporteController {

    private final SoporteService soporte;
    private final PerfilService perfil;

    public SoporteController(SoporteService soporte, PerfilService perfil) {
        this.soporte = soporte;
        this.perfil = perfil;
    }

    @PostMapping("/soporte/consultas")
    public ResponseEntity<SoporteDto.Response> crear(@AuthenticationPrincipal AuthUser user,
                                                     @Valid @RequestBody SoporteDto.Request r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(soporte.crear(perfil.usuario(user.id()), r));
    }

    @GetMapping("/soporte/consultas")
    public List<SoporteDto.Response> mias(@AuthenticationPrincipal AuthUser user) {
        return soporte.mias(user.id());
    }

    @GetMapping("/soporte/consultas/{id}")
    public SoporteDto.Response obtener(@AuthenticationPrincipal AuthUser user, @PathVariable String id) {
        return soporte.obtener(user.id(), id);
    }

    // ----- asesor -----

    @GetMapping("/asesor/consultas")
    public List<SoporteDto.Response> pendientes(@RequestParam(required = false) EstadoConsulta estado) {
        return soporte.pendientes(estado);
    }

    @PostMapping("/asesor/consultas/{id}/tomar")
    public SoporteDto.Response tomar(@PathVariable String id) {
        return soporte.tomar(id);
    }

    @PostMapping("/asesor/consultas/{id}/responder")
    public SoporteDto.Response responder(@AuthenticationPrincipal AuthUser user, @PathVariable String id,
                                         @Valid @RequestBody SoporteDto.RespuestaRequest r) {
        return soporte.responder(user.id(), id, r);
    }
}
