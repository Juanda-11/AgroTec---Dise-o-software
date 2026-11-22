package com.agrotec.service;

import com.agrotec.domain.PlanTipo;
import com.agrotec.domain.Usuario;
import com.agrotec.exception.PlanRequeridoException;
import org.springframework.stereotype.Component;

/** Verifica que el usuario tenga el plan Plus para funciones premium (modelo freemium, RF-14). */
@Component
public class PlanGuard {

    public void requierePlus(Usuario usuario, String funcion) {
        if (usuario.planEfectivo() != PlanTipo.PLUS) {
            throw new PlanRequeridoException(funcion);
        }
    }
}
