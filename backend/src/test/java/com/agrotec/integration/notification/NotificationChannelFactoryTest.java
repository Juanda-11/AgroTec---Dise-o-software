package com.agrotec.integration.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.agrotec.domain.Canal;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotificationChannelFactoryTest {

    private final BandejaSimulada bandeja = new BandejaSimulada();
    private final PushChannel push = new PushChannel(bandeja);
    private final WhatsAppChannel whatsapp = new WhatsAppChannel(bandeja);

    @Test
    void entregaElCanalCorrecto() {
        NotificationChannelFactory fabrica = new NotificationChannelFactory(List.of(push, whatsapp));
        assertSame(push, fabrica.obtener(Canal.PUSH));
        assertSame(whatsapp, fabrica.obtener(Canal.WHATSAPP));
    }

    @Test
    void fallaSiElCanalNoEstaConfigurado() {
        NotificationChannelFactory fabrica = new NotificationChannelFactory(List.of(push));
        assertThrows(IllegalArgumentException.class, () -> fabrica.obtener(Canal.SMS));
    }

    @Test
    void cadaCanalRegistraSuMensajeEnLaBandeja() {
        Destinatario d = new Destinatario("u1", "David", "3001234567");
        whatsapp.enviar(d, "Helada", "Cubre las plantas");
        push.enviar(d, "Lluvia", "Revisa drenajes");
        List<MensajeEnviado> enviados = bandeja.ultimos("u1");
        assertEquals(2, enviados.size());
        assertEquals(Canal.PUSH, enviados.get(0).canal()); // el más reciente primero
        assertEquals("3001234567", enviados.get(1).destino());
    }
}
