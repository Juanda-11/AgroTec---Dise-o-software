package com.agrotec.service;

import com.agrotec.domain.EstadoCultivo;
import com.agrotec.domain.Finca;
import com.agrotec.domain.Usuario;
import com.agrotec.dto.AlertaDto;
import com.agrotec.dto.CultivoDto;
import com.agrotec.dto.InicioDto;
import com.agrotec.exception.BusinessException;
import com.agrotec.integration.weather.CurrentWeather;
import com.agrotec.integration.weather.Forecast;
import com.agrotec.repository.AlertaRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/** Arma el resumen del día para la pantalla de inicio (RF-15) combinando varios servicios. */
@Service
public class InicioService {

    private final PerfilService perfil;
    private final ClimaService clima;
    private final AlertaService alertas;
    private final AlertaRepository alertaRepo;
    private final CultivoService cultivos;
    private final PrecioService precios;

    public InicioService(PerfilService perfil, ClimaService clima, AlertaService alertas, AlertaRepository alertaRepo,
                         CultivoService cultivos, PrecioService precios) {
        this.perfil = perfil;
        this.clima = clima;
        this.alertas = alertas;
        this.alertaRepo = alertaRepo;
        this.cultivos = cultivos;
        this.precios = precios;
    }

    public InicioDto.Response resumen(Usuario u) {
        Finca f = perfil.finca(u.getId());
        String ubicacion = null;
        CurrentWeather actual = null;
        Instant actualizado = null;
        if (f != null) {
            ubicacion = f.getNombre() + " · " + f.getMunicipio();
            try {
                Forecast fc = clima.forecast(f);
                actual = fc.actual();
                actualizado = fc.obtenidoEn();
            } catch (BusinessException e) {
                // sin ubicación utilizable: el inicio sigue mostrándose sin el bloque de clima
            }
        }
        long sinLeer = alertaRepo.countByUsuarioIdAndLeidaFalse(u.getId());
        AlertaDto.Response ultima = alertas.listar(u.getId(), null).stream().findFirst().orElse(null);
        List<CultivoDto.Response> activos = cultivos.listar(u.getId(), null, EstadoCultivo.ACTIVO).stream().limit(3).toList();
        String plaza = f != null ? f.getMunicipio() : PrecioService.PLAZA_POR_DEFECTO;
        return new InicioDto.Response(u.getNombre(), f != null, ubicacion, actual, actualizado, sinLeer, ultima,
                activos, precios.referencia(plaza), u.planEfectivo());
    }
}
