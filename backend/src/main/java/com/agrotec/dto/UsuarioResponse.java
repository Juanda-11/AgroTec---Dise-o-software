package com.agrotec.dto;

import com.agrotec.domain.PlanTipo;
import com.agrotec.domain.Rol;
import com.agrotec.domain.Usuario;
import java.time.Instant;

/** Vista pública del usuario (nunca incluye el hash de la contraseña). */
public record UsuarioResponse(String id, String nombre, String celular, Rol rol, PlanTipo plan,
                              Instant planVigenteHasta, boolean consentimientoDatos,
                              PreferenciasDto.Response preferencias) {

    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCelular(), u.getRol(), u.planEfectivo(),
                u.getPlanVigenteHasta(), u.isConsentimientoDatos(), PreferenciasDto.Response.de(u.getPreferencias()));
    }
}
