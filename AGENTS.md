# AGENTS.md — AgroTec

Contexto del proyecto para agentes de IA y colaboradores. Léelo antes de proponer o modificar requerimientos, historias de usuario o wireframes.

## 1. ¿Qué es AgroTec?

**AgroTec** es un asistente virtual inteligente para agricultores: una **aplicación móvil sencilla y accesible** que permite organizar los cultivos, recibir alertas climáticas y conocer precios de insumos y productos, para fortalecer la productividad agrícola en **Nariño** y otras regiones de Colombia.

Nació como idea de negocio (Universidad Cooperativa de Colombia, 2.º semestre; autor: Juan David Jurado Paz) y se desarrolla ahora como proyecto final de la materia **Diseño de Software**.

## 2. Objetivo general

Desarrollar una aplicación móvil sencilla y accesible que permita a los agricultores organizar sus cultivos, recibir alertas climáticas y conocer precios de insumos, fortaleciendo la productividad agrícola en Nariño y otras regiones.

## 3. Usuario objetivo

Pequeños y medianos agricultores de zonas rurales con acceso limitado a información agrícola y conectividad reducida.

**Persona (ficha buyer): David Mateo Torres** — 35 años, ingeniero agrónomo, administrador de una finca cafetera en Pasto (Nariño), casado con dos hijos, ingresos de ~$4.000.000 mensuales.
- *Necesidades:* mejorar la productividad y reducir pérdidas por clima; información confiable de mercados y precios para vender mejor; aprender herramientas digitales que lo conecten con compradores directos; ahorrar tiempo en procesos administrativos.
- *Frustraciones:* no tener datos confiables del clima y perder cosechas; depender de intermediarios que pagan poco; sentir que las tecnologías son difíciles de usar.
- *Comportamiento:* usa el celular a diario (WhatsApp y redes), confía en las recomendaciones de colegas, asiste a capacitaciones del SENA y Fedecafé.

## 4. Propuesta de valor y diferenciador

Plataforma **ligera, de bajo costo, fácil de usar y de bajo consumo de datos**, pensada para zonas de baja conectividad, con acompañamiento directo al agricultor. Competidores analizados: Frubana, Agrosmart y Merqueo (fuertes en logística, internacionalización o tecnología avanzada); AgroTec se diferencia por **inclusión, simplicidad y acompañamiento**.

## 5. Funcionalidades clave (estrategia de producto)

1. Alertas climáticas.
2. Registro básico de cultivos.
3. Precios de insumos y de productos agrícolas.
4. Funciona con baja conectividad (modo sin conexión).

Complementarias que surgen de la encuesta: notificaciones por WhatsApp, capacitaciones, soporte técnico, suscripción freemium.

## 6. Evidencia de la encuesta (a agricultores)

- 64,3 % considera más valiosos el pronóstico del clima y las alertas de plagas/enfermedades; 57,1 %, los precios de mercado.
- 85,7 % no usa ninguna aplicación agrícola actualmente.
- 35,7 % pagaría una suscripción y 57,1 % «depende del costo»; 64,3 % pagaría entre **$30.000 y $50.000 COP/mes**.
- 92,9 % desea capacitaciones para usar herramientas digitales.
- 57,1 % prefiere **WhatsApp** como canal; 42,9 %, una app móvil.
- Principales obstáculos: falta de confianza en la información digital (50 %), mala conectividad (42,9 %), desconocimiento de las apps (42,9 %).
- Pilares derivados: **funcionar sin conexión, simplicidad de uso y confiabilidad de la información**.

## 7. Modelo de negocio y metas

- Modelo freemium / suscripción de bajo costo.
- Canales: Google Play y App Store; talleres en veredas y asociaciones; emisoras comunitarias y grupos de WhatsApp rurales; Facebook y WhatsApp; charlas con agricultores y UMATAS; alianzas con SENA Agro, cooperativas y alcaldías.
- **Meta de marketing:** al menos 300 usuarios activos en los primeros seis meses.

## 8. Alcance y estado

- Alcance: **análisis y diseño** (requerimientos, historias de usuario, wireframes y las 5 vistas de arquitectura) más una **implementación**: backend obligatorio y frontend opcional.
- Stack (definido por la materia): **backend Java 17 + Spring Boot 3, base de datos MongoDB**; frontend web móvil (PWA) en JavaScript puro, servido por el mismo backend. Despliegue con Docker Compose.
- Integraciones externas **simuladas** (clima por defecto, precios de ejemplo, WhatsApp/SMS/push, pasarela de pagos) detrás de interfaces (`WeatherProvider`, `NotificationChannel`, `PaymentGateway`). No presentarlas como reales.

## 9. Estructura del repositorio

```
AGENTS.md                   Este archivo (contexto del proyecto)
README.md                   Presentación, HU por módulo con enlace a su issue y cómo ejecutar
Dockerfile, docker-compose.yml   Despliegue (API + MongoDB)
backend/                    API Spring Boot (paquete com.agrotec: web, service, domain, repository, integration, ...)
frontend/                   App web móvil (PWA): index.html, css/, js/, sw.js
docs/requerimientos.md      Requerimientos funcionales (RF) y no funcionales (RNF)
docs/historias-usuario.md   Historias de usuario (HU) con trazabilidad a los RF/RNF
docs/wireframes/            Wireframes (mockups) en SVG, uno por pantalla
docs/arquitectura/          Las 5 vistas (contexto, funcional, lógica, desarrollo, despliegue), guía de Visual Paradigm,
                            trazabilidad HU→código y guía de estudio para la sustentación
docs/guia-ejecucion.md      Cómo ejecutar, probar y configurar
```

Las historias de usuario viven también como **issues** de GitHub, cada una con su wireframe.

## 10. Convenciones

- **Idioma:** español (Colombia) en todos los documentos, issues y comentarios.
- **IDs:** requerimientos funcionales `RF-01`, no funcionales `RNF-01`, historias de usuario `HU_001` (tres dígitos, consecutivos por módulo, nunca se reutilizan).
- **Trazabilidad:** cada requerimiento debe tener **al menos una** historia de usuario; cada historia indica su RF/RNF y su wireframe.
- **Historias de usuario:** formato «Como *rol* quiero *acción* para *beneficio*», con criterios de aceptación verificables.
- **Issues:** título `HU_XXX <título>`; cuerpo con Descripción (Como / Quiero / Para), requerimiento asociado, criterios de aceptación numerados y Wireframe incrustado; etiquetas `historia-de-usuario`, el módulo (`modulo-1` … `modulo-7`), el ID del requerimiento (p. ej. `RF-05`) y la prioridad (`prioridad-alta|media|baja`). La pantalla principal (README) lista las HU por módulo con enlace a su issue.
- **Wireframes:** archivos SVG de baja fidelidad (360×720, vista móvil) nombrados `NN-nombre-pantalla.svg`.
- **Lenguaje de producto:** simple y cotidiano; evita tecnicismos en textos de interfaz.

## 11. Reglas para agentes

- Mantén sincronizados `docs/requerimientos.md`, `docs/historias-usuario.md` y las issues: si cambias uno, actualiza los demás.
- No inventes datos de la encuesta ni del mercado; cita solo lo documentado en este archivo.
- No hagas commits, push ni crees/cierres issues sin confirmación explícita del dueño del repositorio.
- No incluyas credenciales ni tokens en el repositorio.
