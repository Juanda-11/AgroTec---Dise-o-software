package com.agrotec.service;

import com.agrotec.domain.Usuario;
import com.agrotec.dto.ActividadDto;
import com.agrotec.dto.AlertaDto;
import com.agrotec.dto.CultivoDto;
import com.agrotec.dto.DatosDto;
import com.agrotec.dto.PerfilDto;
import com.agrotec.dto.SoporteDto;
import com.agrotec.dto.UsuarioResponse;
import com.agrotec.integration.notification.BandejaSimulada;
import com.agrotec.repository.ActividadRepository;
import com.agrotec.repository.AlertaPrecioRepository;
import com.agrotec.repository.AlertaRepository;
import com.agrotec.repository.AvanceCapacitacionRepository;
import com.agrotec.repository.ConsultaSoporteRepository;
import com.agrotec.repository.CultivoRepository;
import com.agrotec.repository.FincaRepository;
import com.agrotec.repository.SuscripcionRepository;
import com.agrotec.repository.UsuarioRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;

/** Derechos del titular de los datos (Ley 1581 de 2012): consultar/exportar y eliminar. RNF-05. */
@Service
public class DatosPersonalesService {

    private final PerfilService perfil;
    private final UsuarioRepository usuarios;
    private final FincaRepository fincas;
    private final CultivoRepository cultivos;
    private final ActividadRepository actividades;
    private final AlertaRepository alertas;
    private final AlertaPrecioRepository alertasPrecio;
    private final ConsultaSoporteRepository consultas;
    private final AvanceCapacitacionRepository avances;
    private final SuscripcionRepository suscripciones;
    private final BandejaSimulada bandeja;

    public DatosPersonalesService(PerfilService perfil, UsuarioRepository usuarios, FincaRepository fincas,
                                  CultivoRepository cultivos, ActividadRepository actividades,
                                  AlertaRepository alertas, AlertaPrecioRepository alertasPrecio,
                                  ConsultaSoporteRepository consultas, AvanceCapacitacionRepository avances,
                                  SuscripcionRepository suscripciones, BandejaSimulada bandeja) {
        this.perfil = perfil;
        this.usuarios = usuarios;
        this.fincas = fincas;
        this.cultivos = cultivos;
        this.actividades = actividades;
        this.alertas = alertas;
        this.alertasPrecio = alertasPrecio;
        this.consultas = consultas;
        this.avances = avances;
        this.suscripciones = suscripciones;
        this.bandeja = bandeja;
    }

    public DatosDto.Exportacion exportar(String usuarioId) {
        Usuario u = perfil.usuario(usuarioId);
        return new DatosDto.Exportacion(Instant.now(), UsuarioResponse.de(u),
                PerfilDto.FincaResponse.de(fincas.findByUsuarioId(usuarioId).orElse(null)),
                cultivos.findByUsuarioIdOrderByActualizadoEnDesc(usuarioId).stream().map(CultivoDto.Response::de).toList(),
                actividades.findByUsuarioIdAndActualizadoEnAfter(usuarioId, Instant.EPOCH).stream()
                        .map(ActividadDto.Response::de).toList(),
                alertas.findByUsuarioIdOrderByCreadaEnDesc(usuarioId).stream().map(AlertaDto.Response::de).toList(),
                consultas.findByUsuarioIdOrderByCreadaEnDesc(usuarioId).stream().map(SoporteDto.Response::de).toList());
    }

    /** Elimina la cuenta y TODOS los datos asociados. No se puede deshacer. */
    public void eliminarCuenta(String usuarioId) {
        perfil.usuario(usuarioId);
        actividades.deleteByUsuarioId(usuarioId);
        cultivos.deleteByUsuarioId(usuarioId);
        fincas.deleteByUsuarioId(usuarioId);
        alertas.deleteByUsuarioId(usuarioId);
        alertasPrecio.deleteByUsuarioId(usuarioId);
        consultas.deleteByUsuarioId(usuarioId);
        avances.deleteByUsuarioId(usuarioId);
        suscripciones.deleteByUsuarioId(usuarioId);
        bandeja.borrar(usuarioId);
        usuarios.deleteById(usuarioId);
    }
}
