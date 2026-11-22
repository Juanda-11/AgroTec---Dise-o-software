# Guía para dibujar los diagramas en Visual Paradigm (versión web)

Esta guía te ayuda a **plasmar** los diagramas tú mismo; así entiendes cada decisión y puedes defenderla.

## Orden recomendado (≈ 2 a 3 horas en total)

1. **Lógica** (clases del dominio): es la más sencilla y la base de las demás. ~40 min.
2. **Contexto** (casos de uso). ~30 min.
3. **Funcional** (componentes). ~30 min.
4. **Desarrollo** (clases por capas y patrones). ~40 min.
5. **Despliegue**. ~20 min.

## Antes de empezar

- Crea **un proyecto** y dentro **cinco diagramas**, uno por vista, con el nombre «Vista X - AgroTec».
- Abre en otra pestaña el documento de la vista correspondiente (este repositorio): la tabla de elementos es tu lista de tareas.
- Dibuja primero **todos los elementos** y después **todas las relaciones**; así no pierdes el hilo.

## Consejos generales

| Tema | Recomendación |
|---|---|
| Nombres | En español y consistentes con el código (`Cultivo`, `AlertaService`) |
| Tamaño | Un diagrama debe leerse en una diapositiva: máximo 12–15 elementos grandes |
| Colores | Máximo 3 o 4; un color por capa o por módulo, siempre con el mismo significado |
| Alineación | Usa la herramienta de alinear/distribuir; evita líneas que se crucen |
| Texto | Letra de al menos 12 pt cuando lo pegues en la diapositiva |
| Exportar | Exporta como **imagen (PNG)** o SVG en alta resolución y pégala en la plantilla `.pptx` |
| Respaldo | Guarda también el proyecto de Visual Paradigm (el enlace o el archivo) |

## Cómo se ve una buena diapositiva

1. Título de la vista (ya viene en la plantilla).
2. El diagrama, grande, ocupando casi toda la diapositiva.
3. Una **leyenda corta** si usas colores o estereotipos.
4. Nada de párrafos: lo que explicas lo dices en voz alta (cada documento trae un guion de 30 segundos).

## Qué suele preguntar el profesor sobre cada diagrama

| Vista | Pregunta típica | Dónde está la respuesta |
|---|---|---|
| Contexto | ¿Por qué este actor es secundario? ¿Qué es un «include»? | `01-vista-contexto.md` |
| Funcional | ¿Qué es una interfaz provista y una requerida? ¿Por qué el clima está detrás de una interfaz? | `02-vista-funcional.md` |
| Lógica | ¿Por qué Actividad es composición de Cultivo y Alerta no? | `03-vista-logica.md` |
| Desarrollo | ¿Qué patrón es este y qué problema resuelve? | `04-vista-desarrollo.md` y `preguntas-profesor.md` |
| Despliegue | ¿Cómo escalarías esto? ¿Qué pasa si cae MongoDB? | `05-vista-despliegue.md` |

## Glosario rápido de UML

- **Actor:** quien interactúa con el sistema desde afuera (persona o sistema).
- **Caso de uso:** una funcionalidad completa vista desde el actor.
- **«include»:** el caso base *siempre* usa al otro (reutilización obligatoria).
- **«extend»:** el caso adicional *a veces* amplía al base.
- **Componente:** módulo con interfaces bien definidas que se puede reemplazar.
- **Interfaz provista / requerida:** lo que un componente ofrece / lo que necesita de otro.
- **Asociación:** relación entre clases. **Composición:** «es parte de» y no existe sin el todo. **Agregación:** «tiene» pero puede existir solo.
- **Realización:** una clase implementa una interfaz. **Dependencia:** una clase usa a otra.
- **Nodo:** hardware o entorno de ejecución. **Artefacto:** archivo que se despliega (jar, imagen, base de datos).
