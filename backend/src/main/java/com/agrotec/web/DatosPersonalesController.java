package com.agrotec.web;

import com.agrotec.dto.DatosDto;
import com.agrotec.security.AuthUser;
import com.agrotec.service.DatosPersonalesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@Tag(name = "Mis datos", description = "RNF-05: derechos del titular (Ley 1581 de 2012)")
public class DatosPersonalesController {

    private final DatosPersonalesService datos;

    public DatosPersonalesController(DatosPersonalesService datos) {
        this.datos = datos;
    }

    @GetMapping("/datos")
    public DatosDto.Exportacion exportar(@AuthenticationPrincipal AuthUser user) {
        return datos.exportar(user.id());
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal AuthUser user) {
        datos.eliminarCuenta(user.id());
        return ResponseEntity.noContent().build();
    }
}
