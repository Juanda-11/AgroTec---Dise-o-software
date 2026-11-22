package com.agrotec.service;

import com.agrotec.domain.Finca;
import com.agrotec.domain.Usuario;
import com.agrotec.dto.PerfilDto;
import com.agrotec.dto.UsuarioResponse;
import com.agrotec.exception.NotFoundException;
import com.agrotec.repository.FincaRepository;
import com.agrotec.repository.UsuarioRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;

/** Perfil del agricultor y datos de su finca (RF-02). */
@Service
public class PerfilService {

    private final UsuarioRepository usuarios;
    private final FincaRepository fincas;

    public PerfilService(UsuarioRepository usuarios, FincaRepository fincas) {
        this.usuarios = usuarios;
        this.fincas = fincas;
    }

    public Usuario usuario(String id) {
        return usuarios.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    public PerfilDto.PerfilResponse obtener(String usuarioId) {
        Usuario u = usuario(usuarioId);
        return new PerfilDto.PerfilResponse(UsuarioResponse.de(u),
                PerfilDto.FincaResponse.de(fincas.findByUsuarioId(usuarioId).orElse(null)));
    }

    public UsuarioResponse actualizarNombre(String usuarioId, PerfilDto.ActualizarRequest r) {
        Usuario u = usuario(usuarioId);
        u.setNombre(r.nombre().trim());
        return UsuarioResponse.de(usuarios.save(u));
    }

    public Finca finca(String usuarioId) {
        return fincas.findByUsuarioId(usuarioId).orElse(null);
    }

    /** Crea o actualiza la finca (un agricultor tiene una finca). */
    public PerfilDto.FincaResponse guardarFinca(String usuarioId, PerfilDto.FincaRequest r) {
        Finca f = fincas.findByUsuarioId(usuarioId).orElseGet(Finca::new);
        f.setUsuarioId(usuarioId);
        f.setNombre(r.nombre().trim());
        f.setDepartamento(r.departamento());
        f.setMunicipio(r.municipio().trim());
        f.setVereda(r.vereda());
        f.setLatitud(r.latitud());
        f.setLongitud(r.longitud());
        f.setActualizadoEn(Instant.now());
        return PerfilDto.FincaResponse.de(fincas.save(f));
    }
}
