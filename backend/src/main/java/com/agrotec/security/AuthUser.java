package com.agrotec.security;

import com.agrotec.domain.Rol;

/** Identidad autenticada extraída del JWT (principal de Spring Security). */
public record AuthUser(String id, String celular, Rol rol) {
}
