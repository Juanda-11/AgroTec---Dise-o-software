package com.agrotec.integration.notification;

import com.agrotec.domain.Canal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Fábrica de canales (patrón FACTORY): entrega la estrategia correcta para un {@link Canal}.
 * Spring inyecta todas las implementaciones de {@link NotificationChannel}, así que agregar un
 * canal nuevo (p. ej. correo) solo requiere crear la clase y un valor en el enum.
 */
@Component
public class NotificationChannelFactory {

    private final Map<Canal, NotificationChannel> canales = new EnumMap<>(Canal.class);

    public NotificationChannelFactory(List<NotificationChannel> implementaciones) {
        implementaciones.forEach(c -> canales.put(c.canal(), c));
    }

    public NotificationChannel obtener(Canal canal) {
        NotificationChannel c = canales.get(canal);
        if (c == null) {
            throw new IllegalArgumentException("No hay un canal configurado para " + canal);
        }
        return c;
    }
}
