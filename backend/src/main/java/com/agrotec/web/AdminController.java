package com.agrotec.web;

import com.agrotec.dto.PrecioDto;
import com.agrotec.service.PrecioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administración", description = "Solo rol ADMIN")
public class AdminController {

    private final PrecioService precios;

    public AdminController(PrecioService precios) {
        this.precios = precios;
    }

    /** Carga un precio nuevo y dispara las alertas de precio objetivo. */
    @PostMapping("/precios")
    public PrecioDto.Item cargarPrecio(@Valid @RequestBody PrecioDto.IngestaRequest r) {
        return precios.ingestar(r);
    }
}
