# Ejercicios de modificación (como los puede pedir el profesor)

Los 8 se compilaron juntos y pasaron las pruebas automáticas. Practica **uno a la vez** y deshaz antes de seguir.

Deshacer todo lo que cambiaste (cuidado: borra tus cambios sin guardar en commits):

```powershell
git checkout -- backend frontend
```

Si creaste un archivo nuevo (ejercicio 6), bórralo a mano. Después de cada cambio detén la app (Ctrl+C) y vuelve a arrancarla.

## 1. Cambiar la temperatura de la alerta de helada

**Lo que te piden:** «Quiero que avise desde 5 °C y no desde 3 °C.»

En `backend/src/main/java/com/agrotec/service/rules/ReglasClimaticas.java`, cambia:

```java
HELADA_C = 3.0;
```
por:

```java
HELADA_C = 5.0;
```

**Comprobar:** `.\mvnw.cmd test` y revisar alertas desde la app.

## 2. Cambiar el precio del plan Plus

**Lo que te piden:** «El plan ahora cuesta 35.000.»

En `backend/src/main/java/com/agrotec/service/SuscripcionService.java`, cambia:

```java
PRECIO_PLUS = 30_000L;
```
por:

```java
PRECIO_PLUS = 35_000L;
```

En `frontend/js/app.js`, cambia:

```javascript
cop.format(30000)
```
por:

```javascript
cop.format(35000)
```

**Comprobar:** Postman → «Ver planes»; en la app, pantalla Planes.

## 3. Agregar un campo a los cultivos (de punta a punta)

**Lo que te piden:** «Quiero guardar quién es el responsable del cultivo.»

En `backend/src/main/java/com/agrotec/domain/Cultivo.java`, cambia:

```java
    private String notas;
```
por:

```java
    private String notas;
    private String responsable;
```

En `backend/src/main/java/com/agrotec/domain/Cultivo.java`, cambia:

```java
    public String getNotas() { return notas; }
```
por:

```java
    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
    public String getNotas() { return notas; }
```

En `backend/src/main/java/com/agrotec/dto/CultivoDto.java`, cambia:

```java
            @Size(max = 500) String notas) {
```
por:

```java
            @Size(max = 500) String notas,
            @Size(max = 60) String responsable) {
```

En `backend/src/main/java/com/agrotec/dto/CultivoDto.java`, cambia:

```java
                           String notas, Instant actualizadoEn) {
```
por:

```java
                           String notas, Instant actualizadoEn, String responsable) {
```

En `backend/src/main/java/com/agrotec/dto/CultivoDto.java`, cambia:

```java
                    c.getActualizadoEn());
```
por:

```java
                    c.getActualizadoEn(), c.getResponsable());
```

En `backend/src/main/java/com/agrotec/service/CultivoService.java`, cambia:

```java
        c.setNotas(r.notas());
```
por:

```java
        c.setNotas(r.notas());
        c.setResponsable(r.responsable());
```

En `frontend/js/app.js`, cambia:

```javascript
    '<label for="notas">Notas (opcional)</label>
```
por:

```javascript
    '<label for="responsable">Responsable</label><input id="responsable" value="' + esc(c.responsable) + '">' +
    '<label for="notas">Notas (opcional)</label>
```

En `frontend/js/app.js`, cambia:

```javascript
fechaCosechaEstimada: $('#cosecha').value || null, notas: $('#notas').value || null
```
por:

```javascript
fechaCosechaEstimada: $('#cosecha').value || null, notas: $('#notas').value || null, responsable: $('#responsable').value || null
```

**Comprobar:** Crear un cultivo en Postman añadiendo `"responsable": "Ana"` y consultarlo. El recorrido es: entidad → DTO → servicio → pantalla.

## 4. Agregar una validación

**Lo que te piden:** «El área no puede superar 1000 hectáreas.»

En `backend/src/main/java/com/agrotec/dto/CultivoDto.java`, cambia:

```java
            @Positive(message = "El área debe ser mayor que cero") Double areaHa,
```
por:

```java
            @Positive(message = "El área debe ser mayor que cero")
            @jakarta.validation.constraints.DecimalMax(value = "1000", message = "El área máxima es 1000 ha") Double areaHa,
```

**Comprobar:** Postman: enviar `areaHa: 5000` y esperar **400** con el mensaje. Para **quitar** una validación, borra su anotación.

## 5. Crear un endpoint nuevo

**Lo que te piden:** «Quiero saber cuántos cultivos tengo.»

En `backend/src/main/java/com/agrotec/web/CultivoController.java`, cambia:

```java
    @GetMapping("/{id}")
```
por:

```java
    @GetMapping("/total")
    public java.util.Map<String, Integer> total(@AuthenticationPrincipal AuthUser user) {
        return java.util.Map.of("total", cultivos.listar(user.id(), null, null).size());
    }

    @GetMapping("/{id}")
```

**Comprobar:** Postman: `GET {{baseUrl}}/api/cultivos/total` con el token.

## 6. Agregar un canal de notificación (correo)

**Lo que te piden:** «¿Qué tan fácil es agregar correo? Muéstrame.» (Strategy + Factory)

En `backend/src/main/java/com/agrotec/domain/Canal.java`, cambia:

```java
PUSH, WHATSAPP, SMS
```
por:

```java
PUSH, WHATSAPP, SMS, EMAIL
```

Archivo **nuevo** `backend/src/main/java/com/agrotec/integration/notification/EmailChannel.java`:

```java
package com.agrotec.integration.notification;

import com.agrotec.domain.Canal;
import java.time.Instant;
import org.springframework.stereotype.Component;

/** Correo electrónico (simulado). */
@Component
public class EmailChannel implements NotificationChannel {

    private final BandejaSimulada bandeja;

    public EmailChannel(BandejaSimulada bandeja) {
        this.bandeja = bandeja;
    }

    @Override
    public Canal canal() {
        return Canal.EMAIL;
    }

    @Override
    public void enviar(Destinatario d, String titulo, String mensaje) {
        bandeja.registrar(d.usuarioId(), new MensajeEnviado(Instant.now(), Canal.EMAIL, d.nombre(), titulo, mensaje));
    }
}
```

**Comprobar:** Postman → «Cambiar preferencias» con `"canales": ["EMAIL"]` y luego «Mensajes enviados». Fíjate: no se modificó `NotificacionService` (abierto/cerrado).

## 7. Cambiar los días de pronóstico del plan Gratis

**Lo que te piden:** «El plan Gratis debe mostrar 5 días.»

En `backend/src/main/java/com/agrotec/service/ClimaService.java`, cambia:

```java
DIAS_GRATIS = 3;
```
por:

```java
DIAS_GRATIS = 5;
```

En `backend/src/main/java/com/agrotec/service/SuscripcionService.java`, cambia:

```java
Pronóstico a 3 días
```
por:

```java
Pronóstico a 5 días
```

**Comprobar:** Registrar un usuario nuevo y consultar el clima. Nota: `scripts/prueba-e2e.py` espera 3 días, así que esa comprobación fallará mientras dure el cambio.

## 8. Agregar un municipio

**Lo que te piden:** «Falta el municipio de Tangua.»

En `backend/src/main/java/com/agrotec/util/MunicipiosNarino.java`, cambia:

```java
Map.entry("chachagui", new Punto(1.3600, -77.2833)));
```
por:

```java
Map.entry("chachagui", new Punto(1.3600, -77.2833)),
            Map.entry("tangua", new Punto(1.0947, -77.3936)));
```

En `backend/src/main/java/com/agrotec/web/PerfilController.java`, cambia:

```java
"La Cruz", "Chachagüí");
```
por:

```java
"La Cruz", "Chachagüí", "Tangua");
```

**Comprobar:** Postman → «Listar municipios». Las coordenadas son aproximadas.
