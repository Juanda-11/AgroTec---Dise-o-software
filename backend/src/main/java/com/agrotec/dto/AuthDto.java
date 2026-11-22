package com.agrotec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDto {

    private AuthDto() {
    }

    public record RegistroRequest(
            @NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String nombre,
            @NotBlank(message = "El celular es obligatorio") String celular,
            @NotBlank(message = "La contraseña o PIN es obligatoria")
            @Size(min = 4, max = 72, message = "Debe tener entre 4 y 72 caracteres") String password,
            boolean aceptaDatos) {
    }

    public record LoginRequest(
            @NotBlank(message = "El celular es obligatorio") String celular,
            @NotBlank(message = "La contraseña es obligatoria") String password) {
    }

    public record RecuperarRequest(@NotBlank(message = "El celular es obligatorio") String celular) {
    }

    public record RestablecerRequest(
            @NotBlank String celular,
            @NotBlank(message = "Ingresa el código recibido por SMS") String codigo,
            @NotBlank @Size(min = 4, max = 72, message = "Debe tener entre 4 y 72 caracteres") String nuevaPassword) {
    }

    public record AuthResponse(String token, UsuarioResponse usuario) {
    }

    /** {@code codigoDesarrollo} solo se llena en desarrollo (no hay pasarela SMS real). */
    public record RecuperarResponse(String mensaje, String codigoDesarrollo) {
    }
}
