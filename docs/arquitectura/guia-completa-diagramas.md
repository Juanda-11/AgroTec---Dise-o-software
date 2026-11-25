# Guía completa para dibujar los 5 diagramas en Visual Paradigm

Todo en un solo documento: por cada diagrama tienes **qué crear, todos los elementos con su nombre exacto, todas las conexiones, dónde ubicar cada cosa y una lista final para revisar**. Sigue el orden de las diapositivas.

| # | Diapositiva | Diagrama en Visual Paradigm | Tiempo aprox. |
|---|---|---|---|
| 1 | Vista de contexto | Diagrama de contexto (flujo de datos) | 30 min |
| 2 | Vista funcional | Diagrama de componentes | 40 min |
| 3 | Vista lógica | Diagrama de clases (dominio) | 40 min |
| 4 | Vista de desarrollo | Diagrama de clases por capas y patrones | 45 min |
| 5 | Vista de despliegue | Diagrama de despliegue | 25 min |

## Antes de empezar (válido para los 5)

- En Visual Paradigm Online: **Diagrama → Nuevo → [tipo de diagrama]** y ponle el nombre indicado en cada sección.
- **Primero todos los elementos, después todas las conexiones.** Así no pierdes el hilo.
- Escribe los nombres **exactamente** como aparecen aquí; coinciden con el código y con los documentos.
- Un **estereotipo** es una etiqueta entre `« »` que aclara qué tipo de cosa es un elemento (en Visual Paradigm se escribe en el campo *Stereotype*, no como texto suelto).
- Exporta cada diagrama como **PNG** (o SVG) y pégalo en la diapositiva de la plantilla.
- Revisa la ortografía antes de exportar. Errores frecuentes: *notificación*, *JWT*, *Gestionar*.

---

# 1. Vista de contexto — Diagrama de contexto (flujo de datos)

### Qué debe tener
Es un **diagrama de contexto** (flujo de datos de nivel 0), como el ejemplo de la plantilla: **el sistema en un círculo central**, las entidades externas en rectángulos y **flechas con el nombre del dato** que entra o sale. No muestra pantallas, bases de datos ni módulos internos.

### Entidades y flujos

| Entidad | Tipo | Envía a AgroTec | Recibe de AgroTec |
|---|---|---|---|
| Agricultor | Persona | Registro, finca, cultivos, consultas y pagos | Clima, alertas, precios y capacitaciones |
| Asesor técnico | Persona | Respuestas técnicas | Consultas de agricultores |
| Administrador | Persona | Precios de mercado | Confirmación de carga |
| Pasarela de pagos | Sistema externo | Resultado y referencia del pago | Solicitud de cobro |
| Proveedor meteorológico | Sistema externo | Pronóstico del clima | Ubicación de la finca |
| Fuente de precios SIPSA-DANE | Sistema externo | Precios de productos e insumos | — |
| Mensajería WhatsApp / SMS / Push | Sistema externo | — | Alertas, códigos y avisos de pago |

> El proveedor del clima, la fuente de precios, la pasarela de pagos y la mensajería están **simulados** en el código (Open-Meteo es opcional). Dilo si te preguntan.

### Qué decir (30 segundos)
> «AgroTec es el sistema central. El agricultor le entrega sus datos de finca y cultivos y recibe clima, alertas y precios. El asesor responde consultas y el administrador carga precios. Hacia afuera, AgroTec pide el pronóstico a un proveedor meteorológico, recibe precios de SIPSA-DANE, cobra con una pasarela de pagos y envía avisos por mensajería.»

### Preguntas probables
- **¿Qué diferencia hay con un diagrama de casos de uso?** El de contexto muestra **qué datos** entran y salen del sistema; el de casos de uso muestra **qué funciones** usa cada actor.
- **¿Por qué la base de datos no aparece?** Es parte interna del sistema; el contexto solo muestra lo que está fuera.
- **¿Por qué la fuente de precios tiene una sola flecha?** El sistema solo recibe sus datos; no le envía nada.
- **¿Dónde se ve en el código?** Clima: `integration/weather`; pagos: `integration/payment`; mensajería: `integration/notification`; precios: `web/AdminController` (carga) y `config/DataSeeder` (datos de ejemplo).

---

# 2. Vista funcional — Diagrama de componentes

**Nombre del diagrama:** `Vista funcional - AgroTec`
**Qué responde:** ¿de qué partes funcionales se compone el sistema y cómo se comunican? Muestra módulos, no clases.

## 2.1 Qué es cada cosa (para no confundirte)
- **Componente:** una pieza del sistema con una responsabilidad clara y que se puede reemplazar. Se dibuja como un rectángulo con el símbolo de componente.
- **Interfaz:** un **contrato**: lista lo que un componente ofrece sin decir cómo lo hace. Es como el enchufe de una pared: no importa de qué central viene la electricidad, importa que el enchufe sea el mismo.
- **Elementos externos también son componentes.** Lo que no programaste (MongoDB, el clima, los pagos) se dibuja como componente, con otro color y un estereotipo distinto.

## 2.2 Contenedores grandes (2)
1. `App web móvil (PWA)` — a la izquierda
2. `AgroTec API (Spring Boot)` — en el centro, más grande

Los componentes de las secciones siguientes se **anidan** (se arrastran dentro) de su contenedor.

## 2.3 Componentes dentro de la PWA (4)

| Componente | Qué hace |
|---|---|
| `Vistas SPA` | Las pantallas de la app |
| `Cliente API` | Hace las llamadas HTTP al servidor |
| `Caché local y bandeja de salida` | Guarda datos y cambios pendientes para trabajar sin conexión |
| `Service Worker` | Hace que la app abra sin conexión |

## 2.4 Componentes dentro de la API (8)

| Componente | Qué hace |
|---|---|
| `Controladores REST` | Reciben las peticiones HTTP |
| `Seguridad JWT` | Verifica quién es el usuario y qué puede hacer |
| `Servicios de aplicación` | La lógica de negocio de cada módulo |
| `Motor de reglas` | Detecta heladas, lluvias, sequías y riesgo de plagas |
| `Notificaciones y eventos` | Envía alertas por push, WhatsApp o SMS |
| `Tareas programadas` | Recordatorios y revisión de alertas cada hora |
| `Repositorios` | Acceso a la base de datos |
| `Adaptadores externos` | Conexión con el clima y los pagos |

## 2.5 Componentes externos (4), a la derecha y fuera de la API

| Componente | Estereotipo | Color sugerido |
|---|---|---|
| `MongoDB` | `«database»` | Gris |
| `Proveedor meteorológico` | `«servicio externo»` | Blanco |
| `WhatsApp / SMS / Push` | `«servicio externo»` | Blanco |
| `Pasarela de pagos` | `«servicio externo»` | Blanco |

## 2.6 Conexiones (checklist) — dependencia: flecha punteada con punta abierta

**Dentro de la PWA**
- [ ] `Vistas SPA` → `Cliente API`
- [ ] `Cliente API` → `Caché local y bandeja de salida`
- [ ] `Service Worker` → `Vistas SPA` — etiqueta: «carcasa»

**Entre la PWA y la API**
- [ ] `Cliente API` → `Controladores REST` — etiqueta: «REST / JSON sobre HTTPS» (en línea recta, es la más importante)

**Dentro de la API**
- [ ] `Controladores REST` → `Seguridad JWT`
- [ ] `Controladores REST` → `Servicios de aplicación`
- [ ] `Servicios de aplicación` → `Motor de reglas`
- [ ] `Servicios de aplicación` → `Repositorios`
- [ ] `Servicios de aplicación` → `Adaptadores externos`
- [ ] `Servicios de aplicación` → `Notificaciones y eventos` — etiqueta: «evento de alerta»
- [ ] `Tareas programadas` → `Servicios de aplicación`

**Hacia los externos**
- [ ] `Repositorios` → `MongoDB` — etiqueta: «driver MongoDB»
- [ ] `Adaptadores externos` → `Proveedor meteorológico` — etiqueta: «HTTPS»
- [ ] `Adaptadores externos` → `Pasarela de pagos` — etiqueta: «HTTPS»
- [ ] `Notificaciones y eventos` → `WhatsApp / SMS / Push` — etiqueta: «HTTPS»

## 2.7 Interfaces (lo que más puntúa en esta vista)

Dibuja **cuatro** (la cuarta es `PaymentGateway`, ofrecida por `Adaptadores externos` y requerida por `Servicios de aplicación`). Cada una tiene un componente que la **ofrece** (círculo) y otro que la **necesita** (semicírculo):

| Interfaz | La ofrece (círculo) | La necesita (semicírculo) | Para qué sirve |
|---|---|---|---|
| `API REST` | `Controladores REST` | `Cliente API` | Contrato entre la app y el servidor |
| `WeatherProvider` | `Adaptadores externos` | `Servicios de aplicación` | Pedir el pronóstico sin saber de qué proveedor viene |
| `NotificationChannel` | `Notificaciones y eventos` | `Servicios de aplicación` | Enviar una alerta sin saber por qué canal sale |

```
 Adaptadores externos ──(○ WeatherProvider)──(◖ Servicios de aplicación
      ofrece (círculo)                         necesita (semicírculo)
```

**Si tu versión no tiene el símbolo del semicírculo**, usa esta variante equivalente: una caja pequeña con el estereotipo `«interface»` y el nombre de la interfaz; del componente que la ofrece, una flecha **punteada con triángulo vacío** (realización), y desde el que la necesita, una flecha **punteada con punta abierta** (dependencia).

## 2.8 Ubicación

```
 ┌──────── PWA ────────┐      ┌───────────── AgroTec API ─────────────┐     ┌ MongoDB
 │ Service Worker      │      │ Controladores ─► Seguridad JWT        │ ──► ├ Proveedor meteorológico
 │ Vistas SPA          │ ───► │      ▼                                │     ├ Pasarela de pagos
 │ Cliente API         │ REST │ Servicios ─► Motor de reglas          │     └ WhatsApp / SMS / Push
 │ Caché local         │      │      ├─► Repositorios / Adaptadores / Notificaciones
 └─────────────────────┘      │ Tareas programadas ─► Servicios       │
                              └───────────────────────────────────────┘
```

## 2.9 Revisión final
- [ ] 12 componentes internos + 4 externos
- [ ] Ortografía: «Seguridad **JWT**»
- [ ] Las 15 conexiones de la lista, con sus etiquetas
- [ ] 4 interfaces
- [ ] Externos con otro color y estereotipo

## 2.10 Qué decir (30 segundos)
> «Dividimos el sistema en una app web móvil y una API. La app guarda datos localmente para trabajar sin señal. En la API, los controladores atienden las peticiones, los servicios aplican la lógica y las reglas de alertas, y los repositorios hablan con MongoDB. Todo lo externo —clima, mensajería y pagos— está detrás de interfaces, así que podemos cambiar de proveedor sin tocar el resto.»

## 2.11 Si el profesor pregunta
- **¿Qué es una interfaz provista y una requerida?** La provista es lo que un componente ofrece; la requerida, lo que necesita de otro. Así se conectan sin conocerse por dentro.
- **¿Por qué el clima va detrás de una interfaz?** Para cambiar de proveedor (de simulado a Open-Meteo o al IDEAM) sin tocar la lógica del negocio.
- **¿Qué diferencia hay con la vista de desarrollo?** Esta muestra las **partes funcionales** y cómo se comunican; la de desarrollo muestra **cómo está organizado el código** (capas, clases, patrones).
- **¿Por qué el caché está en el cliente?** Para que funcione con poca señal, que es el diferenciador de AgroTec.

---

# 3. Vista lógica — Diagrama de clases del dominio

### Qué debe tener
12 clases con atributos **y métodos**, 11 enumeraciones y 23 relaciones (12 entre clases y 11 dependencias hacia enumeraciones). Coincide uno a uno con `backend/src/main/java/com/agrotec/domain/`.

El atributo `preferencias` de `Usuario` no se escribe en la caja: se dibuja como la composición hacia `PreferenciasNotificacion`. La enumeración `Tendencia` (sube, baja, estable) no se dibuja: no se guarda, solo se calcula al responder la API de precios.

### Clases (atributos y métodos)

**Usuario**
```
-id: String
-celular: String
-nombre: String
-passwordHash: String
-rol: Rol
-consentimientoDatos: boolean
-consentimientoFecha: Instant
-plan: PlanTipo
-planVigenteHasta: Instant
-codigoRecuperacionHash: String
-codigoRecuperacionExpira: Instant
-intentosFallidos: int
-bloqueadoHasta: Instant
-creadoEn: Instant
--
+planEfectivo(): PlanTipo
+estaBloqueado(ahora): boolean
+registrarIntentoFallido(ahora, maximo, bloqueo)
+reiniciarIntentos()
+activarPlus(hasta)
```

**PreferenciasNotificacion**
```
-canales: Set<Canal>
-tipos: Set<TipoAlerta>
-numeroWhatsapp: String
-silencioInicio: String
-silencioFin: String
--
+permite(tipo): boolean
+estaEnSilencio(hora): boolean
```

**Finca**
```
-id: String
-usuarioId: String
-nombre: String
-departamento: String
-municipio: String
-vereda: String
-latitud: Double
-longitud: Double
-actualizadoEn: Instant
--
+tieneUbicacion(): boolean
```

**Cultivo**
```
-id: String
-usuarioId: String
-clienteId: String
-tipo: String
-lote: String
-variedad: String
-areaHa: Double
-fechaSiembra: LocalDate
-fechaCosechaEstimada: LocalDate
-estado: EstadoCultivo
-notas: String
-actualizadoEn: Instant
--
+cambiarEstado(nuevo)
+estaActivo(): boolean
```

**Actividad**
```
-id: String
-usuarioId: String
-cultivoId: String
-clienteId: String
-tipo: TipoActividad
-fecha: LocalDate
-nota: String
-recordatorio: Instant
-recordatorioEnviado: boolean
-completada: boolean
-actualizadoEn: Instant
--
+completar()
+marcarRecordatorioEnviado()
```

**Alerta**
```
-id: String
-usuarioId: String
-tipo: TipoAlerta
-nivel: NivelRiesgo
-titulo: String
-mensaje: String
-recomendacion: String
-cultivoId: String
-fuente: String
-clave: String
-creadaEn: Instant
-leida: boolean
--
+marcarLeida()
```

**AlertaPrecio**
```
-id: String
-usuarioId: String
-producto: String
-plaza: String
-precioObjetivo: long
-activa: boolean
-ultimaNotificacion: LocalDate
--
+seCumple(precio, fecha): boolean
+marcarNotificada(fecha)
```

**PrecioMercado**
```
-id: String
-producto: String
-categoria: CategoriaPrecio
-plaza: String
-unidad: String
-precio: long
-fecha: LocalDate
-fuente: String
--
+variacionRespecto(anterior): double
```

**Capacitacion**
```
-id: String
-titulo: String
-descripcion: String
-cultivo: String
-formato: FormatoCapacitacion
-duracionMin: int
-nivel: String
-entidad: String
-url: String
-descargable: boolean
--
+aplicaA(cultivoBuscado): boolean
```

**AvanceCapacitacion**
```
-id: String
-usuarioId: String
-capacitacionId: String
-porcentaje: int
-actualizadoEn: Instant
--
+actualizar(nuevoPorcentaje)
```

**ConsultaSoporte**
```
-id: String
-usuarioId: String
-mensaje: String
-fotos: List<String>
-estado: EstadoConsulta
-respuesta: String
-asesorNombre: String
-creadaEn: Instant
-respondidaEn: Instant
--
+tomar()
+responder(texto, asesor)
```

**Suscripcion**
```
-id: String
-usuarioId: String
-plan: PlanTipo
-cancelada: boolean
-inicio: Instant
-fin: Instant
-metodoPago: MetodoPago
-referenciaPago: String
-monto: long
--
+cancelar()
```

### Relaciones entre clases

| Desde | Hacia | Tipo | Multiplicidad | Nombre |
|---|---|---|---|---|
| Usuario | PreferenciasNotificacion | Composición | 1 — 1 | define |
| Usuario | Finca | Asociación | 1 — 0..1 | posee |
| Usuario | Cultivo | Asociación | 1 — 0..* | registra |
| Cultivo | Actividad | Composición | 1 — 0..* | tiene |
| Usuario | Alerta | Asociación | 1 — 0..* | recibe |
| Alerta | Cultivo | Asociación | 0..* — 0..1 | se refiere a |
| Usuario | AlertaPrecio | Asociación | 1 — 0..* | configura |
| AlertaPrecio | PrecioMercado | Dependencia | — | compara |
| Usuario | AvanceCapacitacion | Asociación | 1 — 0..* | avanza en |
| Capacitacion | AvanceCapacitacion | Asociación | 1 — 0..* | se sigue |
| Usuario | ConsultaSoporte | Asociación | 1 — 0..* | envía |
| Usuario | Suscripcion | Asociación | 1 — 0..* | contrata |

### Enumeraciones (con dependencia punteada desde su clase)

| Enumeración | Valores | Unida a |
|---|---|---|
| `Rol` | AGRICULTOR, ASESOR, ADMIN | Usuario |
| `Canal` | PUSH, WHATSAPP, SMS | PreferenciasNotificacion |
| `TipoAlerta` | CLIMA, PLAGA, PRECIO, LABOR | Alerta |
| `NivelRiesgo` | BAJO, MEDIO, ALTO | Alerta |
| `EstadoCultivo` | ACTIVO, COSECHADO | Cultivo |
| `TipoActividad` | SIEMBRA, RIEGO, FERTILIZACION, CONTROL_PLAGAS, DESHIERBA, COSECHA, OTRA | Actividad |
| `CategoriaPrecio` | PRODUCTO, INSUMO | PrecioMercado |
| `FormatoCapacitacion` | VIDEO, GUIA_PDF | Capacitacion |
| `EstadoConsulta` | ENVIADA, EN_REVISION, RESPONDIDA | ConsultaSoporte |
| `MetodoPago` | NEQUI, DAVIPLATA, TARJETA, PSE | Suscripcion |
| `PlanTipo` | GRATIS, PLUS | Suscripcion |

### Qué decir (30 segundos)
> «El corazón del dominio es el Usuario. Tiene una finca y registra cultivos; cada cultivo guarda sus actividades. El sistema le genera alertas, que se entregan según sus preferencias. Puede configurar alertas de precio que comparan contra los precios del mercado, seguir capacitaciones, consultar a un asesor y contratar el plan Plus. Cada clase tiene sus datos y las reglas que le corresponden.»

### Preguntas probables
- **¿Por qué Actividad es composición de Cultivo y Alerta no?** Si se borra el cultivo, sus actividades se borran con él. La alerta puede existir sin cultivo (una helada afecta a toda la finca).
- **¿Por qué las preferencias están dentro de Usuario?** Siempre se leen con el usuario y no crecen sin límite; en MongoDB se guardan en el mismo documento.
- **¿Por qué las entidades tienen métodos?** La lógica que pertenece a un objeto vive en ese objeto (`AlertaPrecio.seCumple`, `Usuario.estaBloqueado`) y no repartida en los servicios. Los servicios coordinan; las entidades deciden sobre sus propios datos.
- **¿Por qué no hay herencia?** Las diferencias entre tipos (alertas, roles) son de datos, no de comportamiento, así que se modelan con enumeraciones. La realización de interfaces aparece en la vista de desarrollo.
- **¿Por qué hay `usuarioId` además de la línea de asociación?** La línea es la relación conceptual; `usuarioId` es la forma en que MongoDB la guarda (referencia).

---

# 4. Vista de desarrollo — Clases por capas y patrones

**Nombre del diagrama:** `Vista de desarrollo - AgroTec`
**Qué responde:** ¿cómo está organizado el código y qué patrones de diseño usa? Aquí **sí** aparecen controladores, servicios y repositorios.

**Idea clave:** no se dibujan las ~130 clases. Se dibuja **un recorrido completo**: *«el sistema detecta una helada, crea la alerta y la envía por el canal elegido»*. Ese recorrido muestra 4 capas y 5 patrones.

## 4.1 Las 4 capas (paquetes), apiladas de arriba hacia abajo

| Capa | Nombre del paquete | Regla |
|---|---|---|
| 1 | `Presentación (web)` | Recibe HTTP y delega; no tiene lógica |
| 2 | `Aplicación (service)` | Lógica de negocio |
| 3 | `Dominio (domain)` | Entidades del negocio |
| 4 | `Infraestructura (repository, integration)` | Base de datos y servicios externos |

**Regla de dependencia:** cada capa solo usa la de abajo. Los controladores nunca tocan repositorios.

## 4.2 Clases por capa (con estereotipo)

**Capa 1 — Presentación**

| Clase | Estereotipo | Operaciones |
|---|---|---|
| `AlertaController` | `«Controller»` | `+listar()` · `+evaluar()` |
| `CultivoController` | `«Controller»` | `+crear()` |

**Capa 2 — Aplicación**

| Clase | Estereotipo | Operaciones |
|---|---|---|
| `CultivoService` | `«Service»` | `+crear(usuarioId, request)` |
| `AlertaService` | `«Service»` | `+evaluar(usuario)` · `+crear(...)` |
| `ClimaService` | `«Service»` | `+pronostico(usuario)` |
| `NotificacionService` | `«Service»` | `+notificarAlerta(usuario, alerta)` |
| `ReglasClimaticas` | `«Reglas»` | `+evaluar(dias)` |
| `AlertaCreadaEvent` | `«Event»` | — |
| `AlertaCreadaListener` | `«Observer»` | `+alCrearse(evento)` |

**Capa 3 — Dominio**

| Clase | Estereotipo |
|---|---|
| `Cultivo` | `«Entity»` |
| `Alerta` | `«Entity»` |

**Capa 4 — Infraestructura**

| Clase | Estereotipo | Operaciones |
|---|---|---|
| `CultivoRepository` | `«Repository»` | — |
| `AlertaRepository` | `«Repository»` | — |
| `WeatherProvider` | `«interface»` | `+obtener(lat, lon, dias)` |
| `OpenMeteoWeatherProvider` | — | — |
| `SimulatedWeatherProvider` | — | — |
| `NotificationChannel` | `«interface»` | `+canal()` · `+enviar(destinatario, titulo, mensaje)` |
| `PushChannel` | — | — |
| `WhatsAppChannel` | — | — |
| `SmsChannel` | — | — |
| `NotificationChannelFactory` | `«Factory»` | `+obtener(canal)` |

Total: 21 clases.

## 4.3 Relaciones (checklist)

**Dependencias** (flecha punteada con punta abierta, o asociación dirigida)

- [ ] `CultivoController` → `CultivoService`
- [ ] `AlertaController` → `AlertaService`
- [ ] `CultivoService` → `CultivoRepository`
- [ ] `AlertaService` → `AlertaRepository`
- [ ] `AlertaService` → `ClimaService`
- [ ] `AlertaService` → `ReglasClimaticas`
- [ ] `AlertaService` → `AlertaCreadaEvent` — etiqueta: «publica»
- [ ] `AlertaCreadaListener` → `AlertaCreadaEvent` — etiqueta: «escucha»
- [ ] `AlertaCreadaListener` → `NotificacionService`
- [ ] `ClimaService` → `WeatherProvider`
- [ ] `NotificacionService` → `NotificationChannelFactory`
- [ ] `NotificationChannelFactory` → `NotificationChannel`
- [ ] `CultivoRepository` → `Cultivo`
- [ ] `AlertaRepository` → `Alerta`

**Realizaciones** (flecha punteada con **triángulo vacío**, de la clase hacia la interfaz)

- [ ] `OpenMeteoWeatherProvider` → `WeatherProvider`
- [ ] `SimulatedWeatherProvider` → `WeatherProvider`
- [ ] `PushChannel` → `NotificationChannel`
- [ ] `WhatsAppChannel` → `NotificationChannel`
- [ ] `SmsChannel` → `NotificationChannel`

## 4.4 Notas de patrones (lo que más puntúa)

Agrega una **Nota** (Note) pegada a cada grupo con el nombre del patrón y una frase:

| Nota | Pegada a | Texto |
|---|---|---|
| Repository | `CultivoRepository` y `AlertaRepository` | «Oculta MongoDB; los servicios no escriben consultas» |
| Adapter | `WeatherProvider` y sus dos implementaciones | «Aísla al proveedor del clima; se cambia sin tocar la lógica» |
| Strategy | `NotificationChannel` y sus tres canales | «Cada canal envía distinto; se usan igual» |
| Factory | `NotificationChannelFactory` | «Elige el canal según la preferencia del usuario» |
| Observer | `AlertaCreadaEvent` y `AlertaCreadaListener` | «Al crearse una alerta se notifica sin acoplar los servicios» |

## 4.5 Ubicación

```
┌ Presentación ───────────────────────────────────────────────┐
│  CultivoController            AlertaController              │
├ Aplicación ─────────────────────────────────────────────────┤
│  CultivoService   AlertaService ─► ClimaService  ReglasClimaticas
│                        │ publica                              │
│               AlertaCreadaEvent ◄─ AlertaCreadaListener ─► NotificacionService
├ Dominio ────────────────────────────────────────────────────┤
│        Cultivo                     Alerta                     │
├ Infraestructura ────────────────────────────────────────────┤
│  CultivoRepository  AlertaRepository                          │
│  WeatherProvider ◄─ OpenMeteo… / Simulated…                   │
│  NotificationChannelFactory ─► NotificationChannel ◄─ Push / WhatsApp / Sms
└───────────────────────────────────────────────────────────────┘
```

Colores sugeridos: un color por capa (por ejemplo azul, verde, amarillo, gris).

## 4.6 Revisión final
- [ ] 4 paquetes apilados y 21 clases dentro del paquete que les corresponde
- [ ] Estereotipos en las clases
- [ ] 14 dependencias y 5 realizaciones
- [ ] 5 notas de patrones
- [ ] Ninguna flecha sube de una capa de abajo a una de arriba

## 4.7 Qué decir (30 segundos)
> «Organizamos el backend en capas: controladores, servicios, dominio e infraestructura, con dependencias solo hacia abajo. Aplicamos Repository para el acceso a datos, Adapter para el clima, Strategy y Factory para elegir cómo notificar, y Observer para que crear una alerta dispare la notificación sin acoplar los servicios.»

## 4.8 Si el profesor pregunta
- **¿Qué problema resuelve cada patrón?** Repository: separar la base de datos de la lógica. Adapter: cambiar de proveedor sin tocar el negocio. Strategy: varios algoritmos intercambiables (canales). Factory: elegir cuál usar. Observer: reaccionar a un hecho sin acoplar quien lo causa con quien lo atiende.
- **¿Dónde está la inyección de dependencias?** En todo Spring: cada clase recibe sus colaboradores por el constructor.
- **¿Por qué no usaste el patrón X?** Responde con el problema: «aquí no teníamos ese problema; lo usaríamos si…».
- **¿Dónde se aplica el principio abierto/cerrado?** Para agregar un canal (por ejemplo correo) se crea una clase nueva y no se modifica `NotificacionService`.

---

# 5. Vista de despliegue

**Nombre del diagrama:** `Vista de despliegue - AgroTec`
**Qué responde:** ¿en qué equipos y entornos corre cada parte? Usa **nodos** (cajas 3D) y **artefactos** (archivos que se instalan).

## 5.1 Nodos

| Nodo | Estereotipo | Dentro tiene (artefactos) |
|---|---|---|
| `Celular del agricultor` | `«device»` | `App web instalada (PWA)` con HTML, CSS, JS, Service Worker y `localStorage` |
| `Servidor (Docker Compose)` | `«execution environment»` | Los dos contenedores de abajo, y opcionalmente el proxy |
| ↳ `Proxy inverso` (opcional) | `«container»` | Certificado TLS (Nginx o Caddy) |
| ↳ `agrotec-api` | `«container»` | `agrotec-backend-1.0.0.jar` — Java 17, Spring Boot, puerto 8080 |
| ↳ `mongo` | `«container»` | Base de datos `agrotec` — MongoDB 7, puerto 27017, volumen `mongo-data` |
| `Nube (servicios externos)` | `«cloud»` | `Open-Meteo / IDEAM` · `WhatsApp Business / SMS / FCM` · `Pasarela de pagos` |

Anida los contenedores dentro del nodo `Servidor (Docker Compose)`. Los artefactos van dentro del nodo que los ejecuta.

## 5.2 Conexiones (checklist) — asociación etiquetada con el protocolo

- [ ] `Celular del agricultor` — `Proxy inverso` (o directo a `agrotec-api` si no dibujas el proxy) — etiqueta: «HTTPS (REST/JSON)»
- [ ] `Proxy inverso` — `agrotec-api` — etiqueta: «HTTP :8080»
- [ ] `agrotec-api` — `mongo` — etiqueta: «Protocolo MongoDB :27017 (red interna)»
- [ ] `agrotec-api` — `Open-Meteo / IDEAM` — etiqueta: «HTTPS»
- [ ] `agrotec-api` — `WhatsApp Business / SMS / FCM` — etiqueta: «HTTPS»
- [ ] `agrotec-api` — `Pasarela de pagos` — etiqueta: «HTTPS»

## 5.3 Datos útiles para la nota de cada nodo

- **Celular:** Android 8+ o iOS 14+, con navegador moderno.
- **agrotec-api:** imagen `eclipse-temurin:17-jre`; construida con el `Dockerfile` multietapa del repositorio.
- **mongo:** imagen `mongo:7` con volumen para no perder datos al reiniciar.
- **Orquestación:** `docker-compose.yml`; con un solo comando, `docker compose up --build`, se levanta todo.

## 5.4 Ubicación

```
 ┌─ Celular ──────────┐      ┌────── Servidor (Docker Compose) ──────┐      ┌─ Nube ─────────────┐
 │  App web (PWA)     │─HTTPS►│  Proxy inverso ─► agrotec-api ─────────┼─HTTPS►│ Open-Meteo / IDEAM │
 └────────────────────┘      │                       │                │      │ WhatsApp / SMS     │
                             │                  Mongo :27017          │      │ Pasarela de pagos  │
                             │                  (contenedor mongo)    │      └────────────────────┘
                             └────────────────────────────────────────┘
```

## 5.5 Variantes que puedes mencionar (no se dibujan)
- **Producción económica:** una sola máquina virtual con Docker Compose.
- **Producción administrada:** API en un servicio de contenedores y **MongoDB Atlas** como base de datos.
- **Escalado:** como la API no guarda sesión (JWT), se pueden levantar varias réplicas detrás de un balanceador; el caché de pronósticos pasaría a Redis.

## 5.6 Revisión final
- [ ] 3 nodos principales y 2 contenedores anidados (más el proxy si lo dibujas)
- [ ] Artefactos dentro de cada nodo
- [ ] 6 conexiones con el protocolo escrito
- [ ] Notas con la tecnología y el puerto

## 5.7 Qué decir (30 segundos)
> «El agricultor usa la app desde el navegador de su celular, instalable como PWA. Habla por HTTPS con la API de Spring Boot, que corre en un contenedor Docker junto a MongoDB. Con un solo comando se levanta todo. La API llama a servicios externos de clima, mensajería y pagos. Como la API no guarda sesión, se puede escalar con varias réplicas.»

## 5.8 Si el profesor pregunta
- **¿Cómo escalarías esto?** Varias réplicas de la API tras un balanceador, MongoDB en réplica o Atlas, y caché compartido en Redis.
- **¿Qué pasa si cae MongoDB?** La API responde con error en lo que necesite datos; la app sigue mostrando lo guardado en el celular y acumula los cambios para sincronizarlos después.
- **¿Por qué Docker?** Para que funcione igual en cualquier equipo: el mismo comando levanta la API y la base.
- **¿Dónde está la seguridad?** HTTPS en el proxy, tokens JWT firmados, contraseñas con BCrypt y acceso a la base solo desde la red interna.

---

# Lista general antes de exportar

- [ ] Los 5 diagramas tienen el nombre `Vista ... - AgroTec`
- [ ] Revisé la ortografía de todos los nombres
- [ ] Cada diagrama se lee a tamaño de diapositiva (letra de 12 pt o más al pegarlo)
- [ ] Exporté cada uno como PNG o SVG en alta resolución
- [ ] Guardé el proyecto de Visual Paradigm (enlace o archivo)
- [ ] Ensayé el guion de 30 segundos de cada vista
