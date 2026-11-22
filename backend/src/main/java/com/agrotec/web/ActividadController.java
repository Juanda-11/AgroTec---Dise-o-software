package com.agrotec.web;

import com.agrotec.dto.ActividadDto;
import com.agrotec.security.AuthUser;
import com.agrotec.service.ActividadService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Actividades", description = "RF-04: labores y recordatorios de cada cultivo")
public class ActividadController {

    private final ActividadService actividades;

    public ActividadController(ActividadService actividades) {
        this.actividades = actividades;
    }

    @GetMapping("/cultivos/{cultivoId}/actividades")
    public List<ActividadDto.Response> listar(@AuthenticationPrincipal AuthUser user, @PathVariable String cultivoId) {
        return actividades.listar(user.id(), cultivoId);
    }

    @PostMapping("/cultivos/{cultivoId}/actividades")
    public ResponseEntity<ActividadDto.Response> crear(@AuthenticationPrincipal AuthUser user,
                                                       @PathVariable String cultivoId,
                                                       @Valid @RequestBody ActividadDto.Request r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(actividades.crear(user.id(), cultivoId, r));
    }

    @GetMapping("/actividades/pendientes")
    public List<ActividadDto.Response> pendientes(@AuthenticationPrincipal AuthUser user) {
        return actividades.pendientes(user.id());
    }

    @PutMapping("/actividades/{id}")
    public ActividadDto.Response actualizar(@AuthenticationPrincipal AuthUser user, @PathVariable String id,
                                            @Valid @RequestBody ActividadDto.Request r) {
        return actividades.actualizar(user.id(), id, r);
    }

    @PatchMapping("/actividades/{id}/completar")
    public ActividadDto.Response completar(@AuthenticationPrincipal AuthUser user, @PathVariable String id) {
        return actividades.completar(user.id(), id);
    }

    @DeleteMapping("/actividades/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal AuthUser user, @PathVariable String id) {
        actividades.eliminar(user.id(), id);
        return ResponseEntity.noContent().build();
    }
}
