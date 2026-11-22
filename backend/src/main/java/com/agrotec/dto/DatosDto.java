package com.agrotec.dto;

import java.time.Instant;
import java.util.List;

public final class DatosDto {

    private DatosDto() {
    }

    /** Copia de todos los datos personales del usuario (habeas data, RNF-05). */
    public record Exportacion(Instant generadoEn, UsuarioResponse usuario, PerfilDto.FincaResponse finca,
                              List<CultivoDto.Response> cultivos, List<ActividadDto.Response> actividades,
                              List<AlertaDto.Response> alertas, List<SoporteDto.Response> consultas) {
    }
}
