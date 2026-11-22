package com.agrotec.integration.notification;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Bandeja de salida en memoria: guarda los últimos mensajes "enviados" por usuario.
 * Permite demostrar las notificaciones sin contratar WhatsApp Business ni un proveedor de SMS.
 */
@Component
public class BandejaSimulada {

    private static final int MAXIMO = 30;
    private final Map<String, Deque<MensajeEnviado>> porUsuario = new ConcurrentHashMap<>();

    public void registrar(String usuarioId, MensajeEnviado mensaje) {
        Deque<MensajeEnviado> cola = porUsuario.computeIfAbsent(usuarioId, k -> new ArrayDeque<>());
        synchronized (cola) {
            cola.addFirst(mensaje);
            while (cola.size() > MAXIMO) {
                cola.removeLast();
            }
        }
    }

    public List<MensajeEnviado> ultimos(String usuarioId) {
        Deque<MensajeEnviado> cola = porUsuario.get(usuarioId);
        if (cola == null) {
            return List.of();
        }
        synchronized (cola) {
            return new ArrayList<>(cola);
        }
    }

    public void borrar(String usuarioId) {
        porUsuario.remove(usuarioId);
    }
}
