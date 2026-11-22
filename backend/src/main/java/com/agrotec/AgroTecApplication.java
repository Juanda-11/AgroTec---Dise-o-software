package com.agrotec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

// Se excluye el usuario por defecto de Spring Security: la autenticación es solo por JWT (ver SecurityConfig).
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableScheduling
public class AgroTecApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgroTecApplication.class, args);
    }
}
