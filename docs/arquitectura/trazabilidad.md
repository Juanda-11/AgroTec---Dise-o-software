# Trazabilidad: historia de usuario → endpoint → código

Estado: ✅ implementada · 🟡 parcial (ver nota) · todo el backend es el alcance obligatorio; el frontend y los extras suman puntos.

| HU | Endpoints principales | Clases clave | Estado |
|---|---|---|---|
| HU_001 Crear mi cuenta | `POST /api/auth/registro` | `AuthController`, `AuthService.registrar`, `Celulares` | ✅ |
| HU_002 Iniciar sesión y recuperar acceso | `POST /api/auth/login`, `/recuperar`, `/restablecer` | `AuthService` (bloqueo tras 5 intentos, código de 6 dígitos con vigencia de 10 min) | 🟡 El SMS es simulado (el código se devuelve en desarrollo) |
| HU_003 Perfil y finca | `GET/PUT /api/perfil`, `PUT /api/finca`, `GET /api/municipios` | `PerfilService`, `MunicipiosNarino` | ✅ |
| HU_004 Controlar mis datos | `GET /api/me/datos`, `DELETE /api/me` | `DatosPersonalesService` | ✅ |
| HU_005 Registrar cultivo | `POST /api/cultivos` | `CultivoService.crear` (idempotente por `clienteId`) | ✅ |
| HU_006 Consultar, editar, eliminar cultivos | `GET /api/cultivos`, `GET/PUT/DELETE /api/cultivos/{id}`, `PATCH /{id}/estado` | `CultivoService` | ✅ |
| HU_007 Actividades del cultivo | `GET/POST /api/cultivos/{id}/actividades`, `PUT/DELETE /api/actividades/{id}` | `ActividadService` | ✅ |
| HU_008 Recordatorios de labores | `GET /api/actividades/pendientes`, `PATCH /api/actividades/{id}/completar` | `RecordatorioService`, `TareasProgramadas.recordatorios` | ✅ |
| HU_009 Pronóstico del clima | `GET /api/clima` | `ClimaService`, `WeatherProvider` | 🟡 Proveedor simulado por defecto; Open-Meteo disponible con `WEATHER_PROVIDER=openmeteo` |
| HU_010 Alertas climáticas | `GET /api/alertas`, `POST /api/alertas/evaluar` | `AlertaService`, `ReglasClimaticas`, `AlertaCreadaListener` | ✅ Umbrales de ejemplo |
| HU_011 Alertas de plagas | (mismos) | `ReglasPlagas` | 🟡 Reglas didácticas; validar con un agrónomo |
| HU_012 Precios de insumos | `GET /api/precios?categoria=INSUMO` | `PrecioService.listar` | 🟡 Datos de ejemplo (no SIPSA-DANE real) |
| HU_013 Precios y tendencia de productos | `GET /api/precios`, `GET /api/precios/tendencia` | `PrecioService` | 🟡 Datos de ejemplo |
| HU_014 Alerta de precio objetivo | `GET/POST/DELETE /api/precios/alertas`, `POST /api/admin/precios` | `PrecioService.evaluarAlertasPrecio` | ✅ (requiere Plus) |
| HU_015 Usar sin conexión | `GET /api/sync/cambios`; frontend `api.js`, `sw.js` | Caché en `localStorage`, Service Worker | 🟡 Lectura de lo ya visto + alta de cultivos y actividades sin conexión |
| HU_016 Sincronizar datos | `POST /api/sync` | `SyncService`, `SyncMerge` (gana el más reciente) | ✅ |
| HU_017 Ahorrar datos móviles | `GET /api/inicio`, `GET /api/sync/cambios` | Caché de pronóstico (3 h), descarga incremental, una llamada para el inicio | 🟡 Faltan las opciones «solo Wi-Fi» y «sin imágenes» en la app |
| HU_018 Elegir cómo recibir alertas | `GET/PUT /api/notificaciones/preferencias`, `GET /bandeja` | `NotificacionService`, `NotificationChannel`, `NotificationChannelFactory`, `HorarioSilencioso` | 🟡 WhatsApp y SMS simulados (bandeja en memoria) |
| HU_019 Ver capacitaciones | `GET /api/capacitaciones`, `PUT /{id}/avance` | `CapacitacionService` | 🟡 Contenido de ejemplo; sin descarga real de archivos |
| HU_020 Consultar a un asesor | `POST/GET /api/soporte/consultas`, `GET /api/asesor/consultas`, `POST /api/asesor/consultas/{id}/responder` | `SoporteService` | 🟡 Fotos como enlaces (no hay carga de archivos); sin chat en tiempo real |
| HU_021 Planes y suscripción | `GET /api/planes`, `GET/POST/DELETE /api/suscripcion` | `SuscripcionService`, `PaymentGateway` | 🟡 Pasarela simulada |
| HU_022 Resumen del día | `GET /api/inicio` | `InicioService` | ✅ |
| HU_023 Interfaz simple y legible | — (frontend) | Texto de 17 px, iconos con etiqueta, 3 toques máximo | 🟡 Sin prueba con usuarios reales |
| HU_024 Rápida en celulares de gama baja | — | JavaScript puro (sin librerías), carcasa en caché | 🟡 Sin medición formal |
| HU_025 Android e iOS | — | PWA instalable | 🟡 Funciona en navegador; no está publicada en Google Play / App Store |
| HU_026 Fuente y fecha de cada dato | campos `fuente`, `actualizadoEn`, `desactualizado` | `ClimaDto`, `PrecioDto`, `Alerta` | ✅ |

**Seguridad transversal (RNF-05):** JWT firmado (HS256), contraseñas con BCrypt, rutas protegidas por rol (`SecurityConfig`), consentimiento de datos al registrarse, exportación y borrado de datos.
