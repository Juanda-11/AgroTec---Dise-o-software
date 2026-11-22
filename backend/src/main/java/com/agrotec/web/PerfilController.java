package com.agrotec.web;

import com.agrotec.dto.PerfilDto;
import com.agrotec.dto.UsuarioResponse;
import com.agrotec.security.AuthUser;
import com.agrotec.service.PerfilService;
import com.agrotec.util.MunicipiosNarino;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Perfil y finca", description = "RF-02")
public class PerfilController {

    private static final List<String> MUNICIPIOS = List.of("Pasto", "Ipiales", "Tumaco", "Túquerres", "Samaniego",
            "La Unión", "Sandoná", "Buesaco", "Consacá", "Yacuanquer", "Cumbal", "Guachucal", "La Cruz", "Chachagüí");

    public record Municipio(String nombre, double latitud, double longitud) {
    }

    private final PerfilService perfil;

    public PerfilController(PerfilService perfil) {
        this.perfil = perfil;
    }

    @GetMapping("/perfil")
    public PerfilDto.PerfilResponse obtener(@AuthenticationPrincipal AuthUser user) {
        return perfil.obtener(user.id());
    }

    @PutMapping("/perfil")
    public UsuarioResponse actualizar(@AuthenticationPrincipal AuthUser user,
                                      @Valid @RequestBody PerfilDto.ActualizarRequest r) {
        return perfil.actualizarNombre(user.id(), r);
    }

    @PutMapping("/finca")
    public PerfilDto.FincaResponse guardarFinca(@AuthenticationPrincipal AuthUser user,
                                                @Valid @RequestBody PerfilDto.FincaRequest r) {
        return perfil.guardarFinca(user.id(), r);
    }

    /** Municipios de Nariño con coordenadas aproximadas, para elegir la zona sin usar GPS. */
    @GetMapping("/municipios")
    public List<Municipio> municipios() {
        return MUNICIPIOS.stream().map(n -> {
            var p = MunicipiosNarino.buscar(n).orElseThrow();
            return new Municipio(n, p.latitud(), p.longitud());
        }).toList();
    }
}
