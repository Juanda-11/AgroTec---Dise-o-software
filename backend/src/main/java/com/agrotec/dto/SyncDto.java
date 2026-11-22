package com.agrotec.dto;

import com.agrotec.domain.EstadoCultivo;
import com.agrotec.domain.TipoActividad;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/** Contratos de la sincronización con el celular (RF-10). */
public final class SyncDto {

    private SyncDto() {
    }

    public record Cultivo(
            @NotBlank String clienteId,
            @NotBlank String tipo,
            @NotBlank String lote,
            String variedad,
            Double areaHa,
            @NotNull LocalDate fechaSiembra,
            LocalDate fechaCosechaEstimada,
            EstadoCultivo estado,
            String notas,
            @NotNull Instant actualizadoEn) {
    }

    public record Actividad(
            @NotBlank String clienteId,
            String cultivoId,
            String cultivoClienteId,
            @NotNull TipoActividad tipo,
            @NotNull LocalDate fecha,
            String nota,
            Instant recordatorio,
            boolean completada,
            @NotNull Instant actualizadoEn) {
    }

    public record Request(
            @Valid @Size(max = 500) List<Cultivo> cultivos,
            @Valid @Size(max = 2000) List<Actividad> actividades) {
    }

    /** estado: CREADO, ACTUALIZADO, IGNORADO (el servidor tenía un cambio más reciente) o ERROR. */
    public record Resultado(String recurso, String clienteId, String id, String estado, String detalle) {
    }

    public record Response(List<Resultado> resultados, Instant horaServidor) {
    }

    public record Cambios(List<CultivoDto.Response> cultivos, List<ActividadDto.Response> actividades,
                          List<AlertaDto.Response> alertas, Instant horaServidor) {
    }
}
