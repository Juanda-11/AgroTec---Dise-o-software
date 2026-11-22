package com.agrotec.web;

import com.agrotec.domain.CategoriaPrecio;
import com.agrotec.dto.PrecioDto;
import com.agrotec.security.AuthUser;
import com.agrotec.service.PerfilService;
import com.agrotec.service.PrecioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/precios")
@Tag(name = "Precios", description = "RF-08 y RF-09: precios de insumos y productos, tendencia y alertas de precio")
public class PrecioController {

    private final PrecioService precios;
    private final PerfilService perfil;

    public PrecioController(PrecioService precios, PerfilService perfil) {
        this.precios = precios;
        this.perfil = perfil;
    }

    @GetMapping
    public List<PrecioDto.Item> listar(@RequestParam(defaultValue = "PRODUCTO") CategoriaPrecio categoria,
                                       @RequestParam(defaultValue = PrecioService.PLAZA_POR_DEFECTO) String plaza,
                                       @RequestParam(required = false) String q) {
        return precios.listar(categoria, plaza, q);
    }

    @GetMapping("/plazas")
    public List<String> plazas() {
        return PrecioService.PLAZAS;
    }

    @GetMapping("/tendencia")
    public PrecioDto.TendenciaResponse tendencia(@AuthenticationPrincipal AuthUser user,
                                                 @RequestParam String producto,
                                                 @RequestParam(defaultValue = PrecioService.PLAZA_POR_DEFECTO) String plaza,
                                                 @RequestParam(defaultValue = "30") int dias) {
        return precios.tendencia(perfil.usuario(user.id()), producto, plaza, dias);
    }

    @GetMapping("/alertas")
    public List<PrecioDto.AlertaResponse> misAlertas(@AuthenticationPrincipal AuthUser user) {
        return precios.misAlertas(user.id());
    }

    @PostMapping("/alertas")
    public ResponseEntity<PrecioDto.AlertaResponse> crearAlerta(@AuthenticationPrincipal AuthUser user,
                                                                @Valid @RequestBody PrecioDto.AlertaRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(precios.crearAlerta(perfil.usuario(user.id()), r));
    }

    @DeleteMapping("/alertas/{id}")
    public ResponseEntity<Void> eliminarAlerta(@AuthenticationPrincipal AuthUser user, @PathVariable String id) {
        precios.eliminarAlerta(user.id(), id);
        return ResponseEntity.noContent().build();
    }
}
