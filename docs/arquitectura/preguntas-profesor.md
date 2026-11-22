# Guía de estudio: preguntas probables del profesor

Respuestas cortas y honestas. Cuando haya algo simulado o pendiente, **dilo tú primero**: suma credibilidad.

## 1. Sobre el proyecto

**¿Qué problema resuelve AgroTec?**
Los pequeños agricultores de Nariño pierden cosechas por clima y plagas, dependen de intermediarios por no conocer precios y casi no usan herramientas digitales (85,7 % no usa ninguna app, según la encuesta). AgroTec centraliza cultivos, clima, alertas y precios, y funciona con poca conectividad.

**¿Qué es lo que sí funciona y qué está simulado?**
Funciona: registro/login con JWT, gestión de cultivos y actividades, reglas de alertas, sincronización sin conexión, preferencias de notificación, planes freemium y exportación/borrado de datos. **Simulado:** el clima (por defecto; Open-Meteo es opcional), los precios (datos de ejemplo, no SIPSA-DANE), WhatsApp/SMS/push (bandeja en memoria) y la pasarela de pagos. Están detrás de interfaces para reemplazarlos.

## 2. Tecnología

**¿Por qué Spring Boot?** Lo exige la materia y aporta seguridad, validación, acceso a datos y servidor embebido con poca configuración. **¿Por qué MongoDB?** También exigido; además, las entidades son documentos autónomos (cultivo, alerta) y las preferencias de notificación se *embeben* en el usuario porque siempre se leen juntas.

**¿Embeber o referenciar en MongoDB?** Embebí `PreferenciasNotificacion` (se leen siempre con el usuario, no crecen). Referencié cultivos, actividades y alertas por `usuarioId`/`cultivoId` porque crecen sin límite y se consultan por separado.

**¿Por qué REST y JSON?** Es simple, sin estado y lo entiende cualquier cliente móvil. Los códigos HTTP son: 201 al crear, 204 al borrar, 400 datos inválidos, 401 sin sesión, 403 sin plan/rol, 404 no existe, 409 conflicto.

**¿Por qué una PWA y no una app nativa?** Un solo código para Android e iOS, se instala desde el navegador, no depende de las tiendas y funciona sin conexión con Service Worker. Para tiendas se podría empaquetar luego.

## 3. Arquitectura y patrones

**¿Qué arquitectura usaste?** En capas: web → service → repository/integration, con dominio al centro. Cada capa solo depende de la inferior. Facilita pruebas y cambios.

**Explica Strategy y Factory aquí.** `NotificationChannel` es la estrategia (Push, WhatsApp, SMS); `NotificationChannelFactory` devuelve la correcta según el `Canal`. Para agregar correo creo una clase y un valor en el enum; no modifico `NotificacionService` (principio abierto/cerrado).

**Explica Adapter.** `WeatherProvider` define qué necesita el sistema; `OpenMeteoWeatherProvider` y `SimulatedWeatherProvider` adaptan servicios distintos a esa interfaz. Si mañana usamos el IDEAM, solo escribo otro adaptador. Igual con `PaymentGateway`.

**Explica Observer.** `AlertaService` publica `AlertaCreadaEvent`; `AlertaCreadaListener` lo escucha y manda la notificación. `AlertaService` no conoce las notificaciones: bajo acoplamiento.

**¿Qué es el patrón Repository?** Una interfaz por colección (`CultivoRepository`) que oculta MongoDB. Spring Data genera la implementación a partir del nombre de los métodos (`findByIdAndUsuarioId`).

**¿Para qué los DTO?** Para no exponer entidades: el `UsuarioResponse` nunca lleva el `passwordHash`, y el contrato de la API puede cambiar sin tocar la base.

**¿Qué es la inyección de dependencias?** Las clases reciben sus colaboradores por el constructor y Spring los crea y conecta. Permite sustituir implementaciones y probar por separado.

**¿Dónde aplicas SOLID?** Ver `04-vista-desarrollo.md` (S: un servicio por módulo; O: canales; L/I/D: interfaces pequeñas y dependencia de abstracciones).

## 4. Seguridad

**¿Cómo funciona la autenticación?** `POST /api/auth/login` valida celular y contraseña (BCrypt) y devuelve un JWT firmado con HS256 que expira en 24 h. En cada petición, `JwtAuthFilter` lo valida y pone la identidad en el contexto. No hay sesión en el servidor.

**¿Por qué JWT y no sesiones?** Sin estado → se escala con varias réplicas sin compartir sesión. Contra: no se puede revocar un token antes de que expire (se mitigaría con expiración corta y *refresh tokens*).

**¿Cómo evitas que un usuario vea datos de otro?** Todas las consultas filtran por `usuarioId` del token (`findByIdAndUsuarioId`); un cultivo ajeno responde 404.

**¿Qué ataques consideraste?** Fuerza bruta (bloqueo 15 min tras 5 intentos), enumeración de usuarios (la recuperación responde igual exista o no el celular), inyección (Spring Data parametriza consultas), datos inválidos (Bean Validation). **Pendiente:** limitar peticiones (rate limiting), CORS restringido y HTTPS obligatorio en producción.

**¿Y la protección de datos personales?** Ley 1581 de 2012: consentimiento al registrarse, exportación y borrado de datos. Contraseñas con hash BCrypt. En producción, cifrado en reposo en MongoDB y HTTPS.

## 5. Funcionalidad clave

**¿Cómo funciona el modo sin conexión?** El cliente guarda en `localStorage` cada lectura exitosa y, sin red, muestra la última copia. Los cultivos y actividades creados sin conexión van a una bandeja de salida con un `clienteId` (UUID). Al volver la señal, `POST /api/sync` los envía en lote.

**¿Cómo resuelves conflictos?** «Gana el más reciente» (*last-write-wins*) comparando `actualizadoEn` (`SyncMerge`); el servidor recorta fechas futuras del reloj del celular. Es simple; para edición colaborativa intensiva usaría versionado o CRDT.

**¿Qué pasa si el celular reintenta el envío?** Es **idempotente**: el `clienteId` evita duplicados.

**¿Cómo se generan las alertas?** Una tarea programada revisa cada hora el pronóstico de cada finca y aplica `ReglasClimaticas` (helada ≤ 3 °C, lluvia ≥ 30 mm, viento ≥ 40 km/h, 5 días secos) y `ReglasPlagas` (clima × cultivo). La clave (regla + fecha + cultivo) evita alertas duplicadas. Son reglas didácticas que un agrónomo debe validar.

**¿Cómo funciona el freemium?** `PlanGuard.requierePlus` bloquea funciones premium con 403: pronóstico de 7 días, WhatsApp/SMS, tendencias de 90 días, alerta de precio objetivo y asesor técnico. `Usuario.planEfectivo()` considera la fecha de vigencia.

**¿Por qué el pago es simulado?** Una pasarela real (Wompi, PayU) exige cuenta comercial y llaves. `PaymentGateway` define la interfaz; el adaptador simulado permite probar el flujo (incluido el rechazo con el token `RECHAZAR`).

## 6. Calidad y pruebas

**¿Cómo probaste?** Pruebas unitarias de lógica pura: reglas de alertas, horario silencioso, sincronización, normalización de celulares, proveedor simulado y fábrica de canales (`mvn test`). La lógica de negocio importante está en clases sin dependencias de Spring justamente para poder probarla fácil. **Pendiente:** pruebas de integración con MongoDB (Testcontainers) y pruebas de la API.

**¿Qué mejorarías?** Rate limiting, refresh tokens, pruebas de integración, caché distribuido (Redis), carga real de fotos, paginación en listados, métricas (Actuator + Prometheus) y publicar en las tiendas.

## 7. Despliegue y escalabilidad

**¿Cómo se despliega?** `docker compose up --build` levanta la API y MongoDB. Un `Dockerfile` multietapa compila con Maven y ejecuta con un JRE ligero, con usuario sin privilegios.

**¿Cómo escalarías?** La API no guarda estado: varias réplicas tras un balanceador. El caché de pronósticos (en memoria) pasaría a Redis. MongoDB en réplica o en Atlas. Las tareas programadas necesitarían un bloqueo distribuido para no ejecutarse varias veces.

**¿Qué pasa si cae MongoDB?** La API responde 500 en lo que necesite datos; la app sigue mostrando lo guardado localmente y acumula cambios para sincronizar luego.

## 8. Requerimientos

**¿Cómo garantizas la trazabilidad?** Cada requerimiento (RF/RNF) tiene al menos una historia de usuario (issue de GitHub), y cada historia tiene su wireframe y su endpoint (ver `trazabilidad.md`).

**¿Qué requerimientos no funcionales cumpliste y cuáles no?** Seguridad y confiabilidad de la información (fuente y fecha en cada dato) ✅. Bajo consumo de datos 🟡 (caché, descarga incremental; faltan las opciones «solo Wi-Fi»). Rendimiento y usabilidad 🟡 (sin medición con usuarios). Publicación en tiendas ❌ (PWA).
