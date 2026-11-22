package com.agrotec.integration.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SimulatedWeatherProviderTest {

    private final SimulatedWeatherProvider proveedor = new SimulatedWeatherProvider();

    @Test
    void devuelveLosDiasPedidosYEsDeterminista() {
        Forecast a = proveedor.obtener(1.2136, -77.2811, 7);
        Forecast b = proveedor.obtener(1.2136, -77.2811, 7);
        assertEquals(7, a.dias().size());
        assertEquals(a.dias(), b.dias());
        assertEquals(SimulatedWeatherProvider.FUENTE, a.fuente());
    }

    @Test
    void losValoresSonCoherentes() {
        for (ForecastDay d : proveedor.obtener(0.83, -77.64, 7).dias()) {
            assertTrue(d.tempMin() < d.tempMax());
            assertTrue(d.probLluvia() >= 0 && d.probLluvia() <= 100);
            assertTrue(d.humedad() >= 0 && d.humedad() <= 100);
        }
    }
}
