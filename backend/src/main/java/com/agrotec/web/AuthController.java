package com.agrotec.web;

import com.agrotec.dto.AuthDto;
import com.agrotec.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "RF-01: registro, inicio de sesión y recuperación de contraseña")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/registro")
    public ResponseEntity<AuthDto.AuthResponse> registro(@Valid @RequestBody AuthDto.RegistroRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auth.registrar(r));
    }

    @PostMapping("/login")
    public AuthDto.AuthResponse login(@Valid @RequestBody AuthDto.LoginRequest r) {
        return auth.login(r);
    }

    @PostMapping("/recuperar")
    public AuthDto.RecuperarResponse recuperar(@Valid @RequestBody AuthDto.RecuperarRequest r) {
        return auth.solicitarRecuperacion(r);
    }

    @PostMapping("/restablecer")
    public ResponseEntity<Void> restablecer(@Valid @RequestBody AuthDto.RestablecerRequest r) {
        auth.restablecer(r);
        return ResponseEntity.noContent().build();
    }
}
