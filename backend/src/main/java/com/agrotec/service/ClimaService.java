package com.agrotec.service;

import com.agrotec.domain.Finca;
import com.agrotec.domain.PlanTipo;
import com.agrotec.domain.Usuario;
import com.agrotec.dto.ClimaDto;
import com.agrotec.exception.BusinessException;
import com.agrotec.integration.weather.Forecast;
import com.agrotec.integration.weather.WeatherProvider;
import com.agrotec.util.MunicipiosNarino;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * Pronóstico del clima de la finca (RF-05). Guarda en memoria el último pronóstico de cada zona durante
 * 3 horas para ahorrar llamadas al proveedor y datos móviles (RNF-02, RNF-03).
 */
@Service
public class ClimaService {

    private static final Duration VIGENCIA_CACHE = Duration.ofHours(3);
    private static final Duration LIMITE_DESACTUALIZADO = Duration.ofHours(24);
    private static final int DIAS_GRATIS = 3;
    private static final int DIAS_PLUS = 7;

    private final WeatherProvider proveedor;
    private final PerfilService perfil;
    private final Map<String, Forecast> cache = new ConcurrentHashMap<>();

    public ClimaService(WeatherProvider proveedor, PerfilService perfil) {
        this.proveedor = proveedor;
        this.perfil = perfil;
    }

    public ClimaDto.Response pronostico(Usuario usuario) {
        Finca f = perfil.finca(usuario.getId());
        if (f == null) {
            throw new BusinessException("Registra tu finca para ver el clima de tu zona");
        }
        PlanTipo plan = usuario.planEfectivo();
        int dias = plan == PlanTipo.PLUS ? DIAS_PLUS : DIAS_GRATIS;
        Forecast fc = forecast(f);
        boolean viejo = Duration.between(fc.obtenidoEn(), Instant.now()).compareTo(LIMITE_DESACTUALIZADO) > 0;
        String ubicacion = f.getNombre() + " · " + f.getMunicipio();
        return new ClimaDto.Response(ubicacion, fc.actual(), fc.dias().subList(0, Math.min(dias, fc.dias().size())),
                fc.fuente(), fc.obtenidoEn(), viejo, plan, dias);
    }

    /** Pronóstico completo de 7 días de la finca (lo usan también las reglas de alertas). */
    public Forecast forecast(Finca f) {
        double lat;
        double lon;
        if (f.getLatitud() != null && f.getLongitud() != null) {
            lat = f.getLatitud();
            lon = f.getLongitud();
        } else {
            var punto = MunicipiosNarino.buscar(f.getMunicipio()).orElseThrow(() -> new BusinessException(
                    "No pudimos ubicar tu municipio. Marca la ubicación de tu finca en el mapa."));
            lat = punto.latitud();
            lon = punto.longitud();
        }
        String clave = String.format("%.2f,%.2f", lat, lon);
        Forecast guardado = cache.get(clave);
        if (guardado != null && Duration.between(guardado.obtenidoEn(), Instant.now()).compareTo(VIGENCIA_CACHE) < 0) {
            return guardado;
        }
        Forecast nuevo = proveedor.obtener(lat, lon, DIAS_PLUS);
        cache.put(clave, nuevo);
        return nuevo;
    }
}
