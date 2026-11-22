package com.agrotec.dto;

import com.agrotec.domain.ConsultaSoporte;
import com.agrotec.domain.EstadoConsulta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;

public final class SoporteDto {

    private SoporteDto() {
    }

    public record Request(
            @NotBlank(message = "Escribe tu consulta") @Size(max = 1000) String mensaje,
            @Size(max = 3, message = "Puedes adjuntar máximo 3 fotos") List<String> fotos) {
    }

    public record RespuestaRequest(@NotBlank(message = "Escribe la respuesta") @Size(max = 2000) String respuesta) {
    }

    public record Response(String id, String agricultorId, String mensaje, List<String> fotos, EstadoConsulta estado,
                           String respuesta, String asesorNombre, Instant creadaEn, Instant respondidaEn) {
        public static Response de(ConsultaSoporte c) {
            return new Response(c.getId(), c.getUsuarioId(), c.getMensaje(), c.getFotos(), c.getEstado(),
                    c.getRespuesta(), c.getAsesorNombre(), c.getCreadaEn(), c.getRespondidaEn());
        }
    }
}
