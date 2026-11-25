# Arquitectura de AgroTec — las 5 vistas

Documentos para la presentación de **Diseño de Software**. Cada vista corresponde a una diapositiva de la plantilla y a un diagrama que se dibuja en **Visual Paradigm**.

| Diapositiva | Vista | Diagrama en Visual Paradigm | Documento |
|---|---|---|---|
| 2 | Contexto: qué hace el sistema | Diagrama de contexto (flujo de datos, sistema en un círculo) | [01-vista-contexto.md](01-vista-contexto.md) |
| 3 | Funcional | Diagrama de componentes | [02-vista-funcional.md](02-vista-funcional.md) |
| 4 | Lógica | Diagrama de clases (dominio) | [03-vista-logica.md](03-vista-logica.md) |
| 5 | Desarrollo | Diagrama de clases por capas, con patrones | [04-vista-desarrollo.md](04-vista-desarrollo.md) |
| 6 | Despliegue | Diagrama de despliegue | [05-vista-despliegue.md](05-vista-despliegue.md) |

Material de apoyo:

- [guia-completa-diagramas.md](guia-completa-diagramas.md): **empieza aquí**. Los 5 diagramas completos: todos los elementos, todas las conexiones, ubicación y lista de revisión.
- [guia-visual-paradigm.md](guia-visual-paradigm.md): cómo dibujar cada diagrama paso a paso y buenas prácticas de presentación.
- [trazabilidad.md](trazabilidad.md): de cada historia de usuario al endpoint y a la clase que la implementa.
- [preguntas-profesor.md](preguntas-profesor.md): guía de estudio con preguntas probables y respuestas.

> Los diagramas de este repositorio están escritos en **Mermaid** (GitHub los dibuja directamente en esta página). Sirven como **referencia visual**: el entregable son los diagramas que tú dibujes en Visual Paradigm a partir de las tablas de elementos de cada vista.

## Decisiones de arquitectura (resumen)

| Decisión | Elección | Por qué |
|---|---|---|
| Estilo | Monolito modular en capas | Un solo equipo, despliegue simple, bajo costo (el proyecto apunta a zonas con poca conectividad y presupuesto limitado) |
| Backend | Java 17 + Spring Boot 3 | Exigido por la materia; ecosistema maduro (Web, Security, Data) |
| Base de datos | MongoDB | Exigida por la materia; documentos flexibles (preferencias embebidas, datos de campo) |
| API | REST + JSON, sin estado, autenticación con JWT | Escala horizontalmente y funciona bien con clientes móviles |
| Cliente | Web móvil instalable (PWA) en JavaScript puro | Un solo código para Android y iOS; funciona sin conexión con Service Worker |
| Integraciones | Detrás de interfaces (Adapter / Strategy) | Se reemplazan los proveedores simulados por reales sin tocar la lógica |
| Despliegue | Docker Compose (app + MongoDB) | Reproducible en cualquier equipo o servidor |
