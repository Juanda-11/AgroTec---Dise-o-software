package com.agrotec.web;

import com.agrotec.domain.EstadoCultivo;
import com.agrotec.dto.CultivoDto;
import com.agrotec.security.AuthUser;
import com.agrotec.service.CultivoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cultivos")
@Tag(name = "Cultivos", description = "RF-03")
public class CultivoController {

    private final CultivoService cultivos;

    public CultivoController(CultivoService cultivos) {
        this.cultivos = cultivos;
    }

    @GetMapping
    public List<CultivoDto.Response> listar(@AuthenticationPrincipal AuthUser user,
                                            @RequestParam(required = false) String q,
                                            @RequestParam(required = false) EstadoCultivo estado) {
        return cultivos.listar(user.id(), q, estado);
    }

    @PostMapping
    public ResponseEntity<CultivoDto.Response> crear(@AuthenticationPrincipal AuthUser user,
                                                     @Valid @RequestBody CultivoDto.Request r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cultivos.crear(user.id(), r));
    }

    @GetMapping("/{id}")
    public CultivoDto.Response obtener(@AuthenticationPrincipal AuthUser user, @PathVariable String id) {
        return cultivos.obtener(user.id(), id);
    }

    @PutMapping("/{id}")
    public CultivoDto.Response actualizar(@AuthenticationPrincipal AuthUser user, @PathVariable String id,
                                          @Valid @RequestBody CultivoDto.Request r) {
        return cultivos.actualizar(user.id(), id, r);
    }

    @PatchMapping("/{id}/estado")
    public CultivoDto.Response cambiarEstado(@AuthenticationPrincipal AuthUser user, @PathVariable String id,
                                             @RequestParam EstadoCultivo estado) {
        return cultivos.cambiarEstado(user.id(), id, estado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal AuthUser user, @PathVariable String id) {
        cultivos.eliminar(user.id(), id);
        return ResponseEntity.noContent().build();
    }
}
