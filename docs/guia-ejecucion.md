# Guía de ejecución

AgroTec = **backend** (Java 17 + Spring Boot 3 + MongoDB) + **frontend** (web móvil instalable, JavaScript puro) servido por el mismo backend.

## Requisitos

| Opción | Necesitas |
|---|---|
| **A. Docker (la más fácil)** | Docker Desktop |
| **B. Local** | JDK 17 o superior, Maven 3.9+ y MongoDB 7 (o Docker solo para la base) |

## Opción A: Docker

```powershell
docker compose up --build
```

Abre http://localhost:8080 (app) y http://localhost:8080/swagger-ui.html (documentación del API).

## Opción B: local

1. Inicia MongoDB (si no lo tienes instalado, con Docker):

```powershell
docker run -d --name agrotec-mongo -p 27017:27017 mongo:7
```

2. Arranca el backend:

```powershell
cd backend
mvn spring-boot:run
```

3. Abre http://localhost:8080.

También puedes abrir la carpeta `backend` en VS Code o IntelliJ y ejecutar la clase `AgroTecApplication`.

## Usuarios de demostración (solo desarrollo)

Se crean al iniciar si `SEED_DEMO_USERS=true` (valor por defecto). **Desactívalos en producción** (`SEED_DEMO_USERS=false`).

| Rol | Celular | Contraseña | Qué probar |
|---|---|---|---|
| Agricultor (plan Plus, con finca y cultivos) | 3001234567 | 1234 | Todo el flujo del agricultor |
| Asesor técnico | 3000000001 | asesor123 | `GET /api/asesor/consultas`, responder consultas |
| Administrador | 3000000000 | admin123 | `POST /api/admin/precios` |

También puedes **crear tu propia cuenta** desde la pantalla de registro (plan Gratis).

## Variables de entorno

| Variable | Por defecto | Descripción |
|---|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/agrotec` | Conexión a MongoDB |
| `JWT_SECRET` | valor de desarrollo | Clave para firmar tokens (≥ 32 caracteres). **Cámbiala en producción** |
| `WEATHER_PROVIDER` | `simulated` | `simulated` o `openmeteo` (clima real, requiere internet) |
| `SEED_DEMO_USERS` | `true` | Crea los usuarios de demostración |
| `DEV_EXPOSE_RESET_CODE` | `true` | Muestra el código de recuperación en la respuesta (no hay SMS real) |
| `PORT` | `8080` | Puerto HTTP |

## Probar la API (ejemplo)

Iniciar sesión y consultar el clima con el usuario de demostración:

```powershell
$r = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/login -ContentType 'application/json' -Body '{"celular":"3001234567","password":"1234"}'
Invoke-RestMethod -Uri http://localhost:8080/api/clima -Headers @{ Authorization = "Bearer $($r.token)" }
```

O usa Swagger UI: inicia sesión en `/api/auth/login`, copia el token, pulsa **Authorize** y pégalo.

## Pruebas automáticas

```powershell
cd backend
mvn test
```

Son 31 pruebas que no necesitan base de datos:

- **Unitarias:** reglas de alertas (heladas, lluvias, sequía, plagas), resolución de conflictos de sincronización, horario silencioso, normalización de celulares, proveedor de clima simulado y fábrica de canales de notificación.
- **De humo (`ApiSmokeTest`):** levantan todo el contexto de Spring y verifican autenticación (401), autorización por rol (403) y validación de entradas (400).

### Recorrido de punta a punta (con la app y MongoDB corriendo)

Con la aplicación levantada (opción A o B), ejecuta desde la raíz del repositorio:

```powershell
python scripts/prueba-e2e.py
```

Ejercita 59 comprobaciones contra la API real: registro y login, bloqueo y recuperación de contraseña, cultivos y actividades, aislamiento entre usuarios, sincronización (idempotencia y «gana el más reciente»), plan Gratis frente a Plus, pagos aprobados y rechazados, asesor, alertas de precio, recordatorios programados, exportación y borrado de datos. Tarda unos 90 segundos porque espera al programador de tareas. Solo usa la biblioteca estándar de Python.

## Probar el modo sin conexión

1. Inicia sesión y abre la pantalla de **Cultivos** (se guarda una copia local).
2. En las herramientas del navegador (F12 → Network) marca **Offline**.
3. Crea un cultivo: aparece como *Pendiente* y se guarda en el dispositivo.
4. Desmarca Offline: la app sincroniza sola (o pulsa **Sincronizar**) y aparece «Todo al día».

## Ver las notificaciones simuladas

Con el usuario de demostración: **Más → Notificaciones** muestra la bandeja de mensajes «enviados» por push, WhatsApp y SMS. Para generar alertas pulsa **Alertas → Revisar mi finca ahora**.

## Problemas frecuentes

| Problema | Solución |
|---|---|
| `Connection refused` a MongoDB | Verifica que MongoDB esté corriendo en el puerto 27017 |
| `Port 8080 already in use` | Cambia `PORT` o cierra el otro programa |
| Ruta demasiado larga en Windows al clonar | `git config --global core.longpaths true` y clona en una carpeta corta (p. ej. `C:\proyectos`) |
| La app no muestra datos nuevos | Recarga con Ctrl+F5 (el Service Worker guarda la carcasa de la app) |
