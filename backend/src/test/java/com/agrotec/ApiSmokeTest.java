package com.agrotec;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrotec.domain.Rol;
import com.agrotec.domain.Usuario;
import com.agrotec.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Prueba de humo: arranca TODO el contexto de Spring (controladores, servicios, seguridad, repositorios)
 * sin necesitar MongoDB y verifica la capa HTTP que no depende de la base de datos:
 * autenticación, autorización por rol y validación de entradas.
 * Si un bean está mal conectado o una consulta de repositorio es inválida, esta prueba falla al arrancar.
 */
@SpringBootTest(properties = {
        "spring.data.mongodb.auto-index-creation=false",
        "agrotec.seed.enabled=false"
})
@AutoConfigureMockMvc
class ApiSmokeTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtService jwt;

    private String tokenDe(Rol rol) {
        Usuario u = new Usuario();
        u.setId("u-prueba");
        u.setCelular("3001234567");
        u.setRol(rol);
        return jwt.generar(u);
    }

    @Test
    void lasRutasProtegidasExigenToken() throws Exception {
        mvc.perform(get("/api/cultivos")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Inicia sesión para continuar"));
        mvc.perform(get("/api/inicio")).andExpect(status().isUnauthorized());
    }

    @Test
    void unTokenInvalidoEsRechazado() throws Exception {
        mvc.perform(get("/api/cultivos").header("Authorization", "Bearer token-falso"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void elRegistroValidaLosDatosAntesDeTocarLaBase() throws Exception {
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.nombre").exists())
                .andExpect(jsonPath("$.detalles.celular").exists())
                .andExpect(jsonPath("$.detalles.password").exists());
    }

    @Test
    void elRegistroExigeAceptarElTratamientoDeDatos() throws Exception {
        String cuerpo = "{\"nombre\":\"Ana\",\"celular\":\"3001234567\",\"password\":\"1234\",\"aceptaDatos\":false}";
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("tratamiento de datos")));
    }

    @Test
    void unAgricultorNoPuedeUsarLasRutasDeAdministrador() throws Exception {
        mvc.perform(post("/api/admin/precios").header("Authorization", "Bearer " + tokenDe(Rol.AGRICULTOR))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unAgricultorNoPuedeUsarLasRutasDelAsesor() throws Exception {
        mvc.perform(get("/api/asesor/consultas").header("Authorization", "Bearer " + tokenDe(Rol.AGRICULTOR)))
                .andExpect(status().isForbidden());
    }

    @Test
    void laDocumentacionDeLaApiYElFrontendSonPublicos() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
        mvc.perform(get("/index.html")).andExpect(status().isOk());
    }
}
