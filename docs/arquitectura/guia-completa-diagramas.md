# Guía completa para dibujar los 5 diagramas en Visual Paradigm

Todo en un solo documento: por cada diagrama tienes **qué crear, todos los elementos con su nombre exacto, todas las conexiones, dónde ubicar cada cosa y una lista final para revisar**. Sigue el orden de las diapositivas.

| # | Diapositiva | Diagrama en Visual Paradigm | Tiempo aprox. |
|---|---|---|---|
| 1 | Vista de contexto | Diagrama de casos de uso | 30 min |
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

# 1. Vista de contexto — Diagrama de casos de uso

**Nombre del diagrama:** `Vista de contexto - AgroTec`
**Qué responde:** ¿qué hace el sistema y para quién? Es la vista más sencilla: el sistema como una caja, sus usuarios a un lado y los sistemas externos al otro.

## 1.1 Límite del sistema
- Un rectángulo grande (**System**) con el nombre `AgroTec`. Todo lo que hace el sistema va **dentro**; los actores van **fuera**.

## 1.2 Actores (fuera del rectángulo)

| Lado | Actor | Estereotipo |
|---|---|---|
| Izquierda | Agricultor | — |
| Izquierda | Asesor técnico | — |
| Izquierda | Administrador | — |
| Derecha | Proveedor meteorológico | `«sistema externo»` |
| Derecha | Fuente de precios (SIPSA-DANE) | `«sistema externo»` |
| Derecha | Pasarela de pagos | `«sistema externo»` |
| Derecha | WhatsApp / SMS / Push | `«sistema externo»` |

## 1.3 Casos de uso (óvalos dentro del rectángulo): 19 en total

**Del Agricultor (15)**
1. Registrarse e iniciar sesión
2. Configurar perfil y finca
3. Gestionar mis datos personales
4. Gestionar cultivos
5. Registrar actividades y recordatorios
6. Consultar pronóstico del clima
7. Recibir alertas de clima y plagas
8. Consultar precios y tendencias
9. Alerta de precio objetivo
10. Usar sin conexión y sincronizar
11. Configurar notificaciones
12. Ver capacitaciones
13. Consultar a un asesor
14. Suscribirse al plan Plus
15. Ver resumen del día

**Del Asesor técnico (1):** 16. Responder consultas técnicas
**Del Administrador (1):** 17. Cargar precios de mercado
**Internos, solo para las relaciones «include» (2):** 18. Enviar notificación · 19. Procesar pago

## 1.4 Conexiones (checklist)

**Asociaciones** (línea simple, sin flecha)

- [ ] Agricultor — casos 1 al 15 (los 15)
- [ ] Asesor técnico — Responder consultas técnicas
- [ ] Administrador — Cargar precios de mercado
- [ ] Proveedor meteorológico — Consultar pronóstico del clima
- [ ] Proveedor meteorológico — Recibir alertas de clima y plagas
- [ ] Fuente de precios — Consultar precios y tendencias
- [ ] Fuente de precios — Cargar precios de mercado
- [ ] Pasarela de pagos — Procesar pago
- [ ] WhatsApp / SMS / Push — Enviar notificación

**Relaciones «include»** (línea punteada con flecha hacia el caso incluido; escribe `include` en el campo *Stereotype*)

- [ ] Recibir alertas de clima y plagas → Enviar notificación
- [ ] Alerta de precio objetivo → Enviar notificación
- [ ] Registrar actividades y recordatorios → Enviar notificación
- [ ] Suscribirse al plan Plus → Procesar pago

## 1.5 Ubicación para que no se crucen las líneas

```
  Asesor técnico ─┐        ┌──────────── AgroTec ───────────┐
                  ├───────►│  16 Responder consultas ...     │
                  │        │                                 │      ┌ Pasarela de pagos
  Agricultor ─────┼───────►│  1 ... 15   (dos columnas)      │──────┤ WhatsApp / SMS / Push
  (centro vertical)        │  18 Enviar notificación         │      │ Proveedor meteorológico
                  │        │  19 Procesar pago               │      └ Fuente de precios
  Administrador ──┴───────►│  17 Cargar precios              │
                           └─────────────────────────────────┘
```

- Pon el Agricultor en el **centro vertical**, pegado al borde izquierdo, y usa conexiones de **línea recta**.
- Deja cerca de su actor externo los casos que lo usan: *Consultar pronóstico* y *Recibir alertas* hacia el proveedor meteorológico; *Consultar precios* y *Cargar precios* hacia la fuente de precios.

## 1.6 Revisión final
- [ ] 19 óvalos y 7 actores
- [ ] 4 flechas «include» con la etiqueta como estereotipo
- [ ] Sin errores de ortografía («notificación», «Gestionar»)
- [ ] Los 4 sistemas externos tienen `«sistema externo»`

## 1.7 Qué decir (30 segundos)
> «AgroTec ayuda al pequeño agricultor de Nariño a organizar sus cultivos, enterarse del clima y de las plagas antes de que lleguen, y conocer los precios para vender mejor, incluso con poca señal. El asesor responde consultas y el administrador carga precios. El sistema depende de cuatro servicios externos: clima, precios, pagos y mensajería.»

## 1.8 Si el profesor pregunta
- **«include» vs «extend»:** con *include* el caso base **siempre** usa al otro; con *extend* el caso adicional **a veces** lo amplía.
- **¿Por qué el proveedor meteorológico es un actor?** Está fuera del sistema y interactúa con él.
- **¿Dónde están los requerimientos de calidad (rendimiento, seguridad)?** No son acciones de un usuario; son requerimientos no funcionales y se explican aparte.

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

Dibuja **tres**. Cada una tiene un componente que la **ofrece** (círculo) y otro que la **necesita** (semicírculo):

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
- [ ] 3 interfaces
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

**Nombre del diagrama:** `Vista lógica - AgroTec`
**Qué responde:** ¿cuáles son los conceptos del negocio y cómo se relacionan? Solo clases del dominio, sin controladores ni repositorios.

## 3.1 Las 12 clases y sus atributos

Formato en Visual Paradigm: `-nombre: Tipo` (el guion significa privado).

**Usuario**
`-id: String` · `-celular: String` · `-nombre: String` · `-passwordHash: String` · `-rol: Rol` · `-plan: PlanTipo` · `-planVigenteHasta: Instant` · `-consentimientoDatos: boolean`
Operación: `+planEfectivo(): PlanTipo`

**PreferenciasNotificacion**
`-canales: Set<Canal>` · `-tipos: Set<TipoAlerta>` · `-numeroWhatsapp: String` · `-silencioInicio: String` · `-silencioFin: String`

**Finca**
`-id: String` · `-nombre: String` · `-departamento: String` · `-municipio: String` · `-vereda: String` · `-latitud: Double` · `-longitud: Double`

**Cultivo**
`-id: String` · `-tipo: String` · `-lote: String` · `-variedad: String` · `-areaHa: Double` · `-fechaSiembra: LocalDate` · `-fechaCosechaEstimada: LocalDate` · `-estado: EstadoCultivo`

**Actividad**
`-id: String` · `-tipo: TipoActividad` · `-fecha: LocalDate` · `-nota: String` · `-recordatorio: Instant` · `-completada: boolean`

**Alerta**
`-id: String` · `-tipo: TipoAlerta` · `-nivel: NivelRiesgo` · `-titulo: String` · `-mensaje: String` · `-recomendacion: String` · `-fuente: String` · `-leida: boolean`

**AlertaPrecio**
`-id: String` · `-producto: String` · `-plaza: String` · `-precioObjetivo: long` · `-activa: boolean`

**PrecioMercado**
`-id: String` · `-producto: String` · `-categoria: CategoriaPrecio` · `-plaza: String` · `-unidad: String` · `-precio: long` · `-fecha: LocalDate` · `-fuente: String`

**Capacitacion**
`-id: String` · `-titulo: String` · `-cultivo: String` · `-formato: FormatoCapacitacion` · `-duracionMin: int`

**AvanceCapacitacion**
`-id: String` · `-porcentaje: int`

**ConsultaSoporte**
`-id: String` · `-mensaje: String` · `-fotos: List<String>` · `-estado: EstadoConsulta` · `-respuesta: String`

**Suscripcion**
`-id: String` · `-plan: PlanTipo` · `-inicio: Instant` · `-fin: Instant` · `-metodoPago: MetodoPago` · `-monto: long`

> Si una clase queda muy cargada, quita `id` de las secundarias. Los atributos de tipo enumeración (como `rol`) se pueden mostrar solo con el nombre del tipo.

## 3.2 Enumeraciones (dibuja estas 4 con el estereotipo `«enumeration»`)

| Enumeración | Valores |
|---|---|
| `Rol` | AGRICULTOR, ASESOR, ADMIN |
| `PlanTipo` | GRATIS, PLUS |
| `TipoAlerta` | CLIMA, PLAGA, PRECIO, LABOR |
| `Canal` | PUSH, WHATSAPP, SMS |

Las demás (`EstadoCultivo`, `TipoActividad`, `NivelRiesgo`, `CategoriaPrecio`, `FormatoCapacitacion`, `EstadoConsulta`, `MetodoPago`) quedan solo como tipo del atributo.

## 3.3 Relaciones (checklist)

| # | Desde | Hacia | Tipo | Multiplicidad (origen — destino) |
|---|---|---|---|---|
| 1 | Usuario | PreferenciasNotificacion | **Composición** (rombo relleno junto a Usuario) | 1 — 1 |
| 2 | Usuario | Finca | Asociación | 1 — 0..1 |
| 3 | Usuario | Cultivo | Asociación | 1 — 0..* |
| 4 | Cultivo | Actividad | **Composición** (rombo junto a Cultivo) | 1 — 0..* |
| 5 | Usuario | Alerta | Asociación | 1 — 0..* |
| 6 | Alerta | Cultivo | Asociación opcional | 0..* — 0..1 |
| 7 | Usuario | AlertaPrecio | Asociación | 1 — 0..* |
| 8 | AlertaPrecio | PrecioMercado | **Dependencia** (flecha punteada), etiqueta «compara» | — |
| 9 | Usuario | AvanceCapacitacion | Asociación | 1 — 0..* |
| 10 | Capacitacion | AvanceCapacitacion | Asociación | 1 — 0..* |
| 11 | Usuario | ConsultaSoporte | Asociación | 1 — 0..* |
| 12 | Usuario | Suscripcion | Asociación | 1 — 0..* |

Las multiplicidades se escriben con doble clic sobre la línea.

## 3.4 Ubicación (Usuario al centro)

```
   Capacitacion ─── AvanceCapacitacion          Suscripcion
                          \                     /
   ConsultaSoporte ────── USUARIO ────────── Finca
                         /    |     \
        AlertaPrecio ───     Alerta   PreferenciasNotificacion
             |                  |
        PrecioMercado        Cultivo ─── Actividad
```

Colores sugeridos (máximo cuatro): usuarios y finca · cultivos y actividades · alertas y precios · capacitación, soporte y planes.

## 3.5 Revisión final
- [ ] 12 clases con sus atributos
- [ ] 4 enumeraciones con sus valores
- [ ] 12 relaciones con multiplicidades
- [ ] 2 composiciones (rombo relleno) y 1 dependencia punteada
- [ ] Usuario al centro

## 3.6 Qué decir (30 segundos)
> «El corazón del dominio es el Usuario. Tiene una finca y varios cultivos; cada cultivo guarda su historial de actividades. El sistema genera Alertas para el usuario, que se envían según sus preferencias. Los precios de mercado son independientes del usuario, y el agricultor puede fijar alertas de precio objetivo. Además modelamos capacitaciones con su avance, consultas al asesor y suscripciones.»

## 3.7 Si el profesor pregunta
- **¿Por qué Actividad es composición de Cultivo y Alerta no?** Si se borra el cultivo, sus actividades pierden sentido y se borran. La alerta puede existir sin cultivo (una alerta de helada afecta a toda la finca).
- **¿Por qué las preferencias están dentro de Usuario?** Siempre se leen junto con el usuario y no crecen sin límite; en MongoDB se guardan dentro del mismo documento.
- **¿Por qué PrecioMercado no está ligado a Usuario?** Los precios son datos del mercado, iguales para todos; el usuario solo se relaciona con ellos por medio de sus alertas de precio.
- **¿Cuál es la diferencia entre composición y asociación?** En la composición la parte no existe sin el todo; en la asociación, ambas existen por separado.

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
