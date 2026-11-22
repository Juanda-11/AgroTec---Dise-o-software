package com.agrotec.web;

import com.agrotec.dto.CapacitacionDto;
import com.agrotec.security.AuthUser;
import com.agrotec.service.CapacitacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/capacitaciones")
@Tag(name = "Capacitaciones", description = "RF-12")
public class CapacitacionController {

    private final CapacitacionService capacitaciones;

    public CapacitacionController(CapacitacionService capacitaciones) {
        this.capacitaciones = capacitaciones;
    }

    @GetMapping
    public List<CapacitacionDto.Response> listar(@AuthenticationPrincipal AuthUser user,
                                                 @RequestParam(required = false) String cultivo) {
        return capacitaciones.listar(user.id(), cultivo);
    }

    @PutMapping("/{id}/avance")
    public CapacitacionDto.Response avance(@AuthenticationPrincipal AuthUser user, @PathVariable String id,
                                           @Valid @RequestBody CapacitacionDto.AvanceRequest r) {
        return capacitaciones.registrarAvance(user.id(), id, r.porcentaje());
    }
}
