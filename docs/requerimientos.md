# Requerimientos de AgroTec

Derivados del documento de contexto «Idea de negocio — AgroTec». Prioridad: Alta / Media / Baja.

Total: 15 requerimientos funcionales y 6 no funcionales.

## Requerimientos funcionales

| ID | Requerimiento | Prioridad | Historias de usuario |
|---|---|---|---|
| RF-01 | Registro e inicio de sesión | Alta | HU_001, HU_002 |
| RF-02 | Perfil del agricultor y de la finca | Alta | HU_003 |
| RF-03 | Registro de cultivos | Alta | HU_005, HU_006 |
| RF-04 | Actividades y recordatorios del cultivo | Media | HU_007, HU_008 |
| RF-05 | Pronóstico del clima | Alta | HU_009 |
| RF-06 | Alertas climáticas | Alta | HU_010 |
| RF-07 | Alertas de plagas y enfermedades | Media | HU_011 |
| RF-08 | Consulta de precios de insumos | Media | HU_012 |
| RF-09 | Consulta de precios de productos agrícolas | Media | HU_013, HU_014 |
| RF-10 | Funcionamiento sin conexión y sincronización | Alta | HU_015, HU_016 |
| RF-11 | Notificaciones por WhatsApp y SMS | Media | HU_018 |
| RF-12 | Capacitaciones | Media | HU_019 |
| RF-13 | Soporte técnico | Media | HU_020 |
| RF-14 | Planes y suscripción | Baja | HU_021 |
| RF-15 | Panel de inicio | Media | HU_022 |

## Requerimientos no funcionales

| ID | Requerimiento | Prioridad | Historias de usuario |
|---|---|---|---|
| RNF-01 | Usabilidad para usuarios rurales | Alta | HU_023 |
| RNF-02 | Bajo consumo de datos | Alta | HU_017 |
| RNF-03 | Rendimiento | Media | HU_024 |
| RNF-04 | Compatibilidad multiplataforma | Alta | HU_025 |
| RNF-05 | Seguridad y protección de datos personales | Alta | HU_004 |
| RNF-06 | Confiabilidad de la información | Alta | HU_026 |

## Detalle

### RF-01 · Registro e inicio de sesión

- **Tipo:** Funcional
- **Prioridad:** Alta
- **Descripción:** El sistema debe permitir al agricultor crear una cuenta, iniciar sesión y recuperar el acceso usando su número de celular, con el mínimo de pasos posible.
- **Origen en el contexto:** Aplicación móvil sencilla y accesible
- **Historias de usuario:** HU_001, HU_002

### RF-02 · Perfil del agricultor y de la finca

- **Tipo:** Funcional
- **Prioridad:** Alta
- **Descripción:** El sistema debe permitir registrar y editar los datos del agricultor y de su finca (nombre, departamento, municipio, vereda y ubicación geográfica), que se usan para personalizar clima, alertas y precios.
- **Origen en el contexto:** Información personalizada; zonas rurales de Nariño
- **Historias de usuario:** HU_003

### RF-03 · Registro de cultivos

- **Tipo:** Funcional
- **Prioridad:** Alta
- **Descripción:** El sistema debe permitir crear, consultar, editar y eliminar cultivos (tipo, lote, variedad, área, fecha de siembra y de cosecha estimada).
- **Origen en el contexto:** Estrategia 2: registro básico de cultivos; organizar sus cultivos
- **Historias de usuario:** HU_005, HU_006

### RF-04 · Actividades y recordatorios del cultivo

- **Tipo:** Funcional
- **Prioridad:** Media
- **Descripción:** El sistema debe permitir registrar las labores realizadas en cada cultivo (siembra, riego, fertilización, control de plagas, cosecha) y programar recordatorios de labores pendientes.
- **Origen en el contexto:** Organizar los cultivos; ahorrar tiempo en procesos administrativos
- **Historias de usuario:** HU_007, HU_008

### RF-05 · Pronóstico del clima

- **Tipo:** Funcional
- **Prioridad:** Alta
- **Descripción:** El sistema debe mostrar el pronóstico del clima actual y de los próximos 7 días para la ubicación de la finca.
- **Origen en el contexto:** Estrategia 1: alertas climáticas; encuesta (64,3 % prioriza el pronóstico)
- **Historias de usuario:** HU_009

### RF-06 · Alertas climáticas

- **Tipo:** Funcional
- **Prioridad:** Alta
- **Descripción:** El sistema debe notificar al agricultor eventos climáticos que pongan en riesgo sus cultivos (heladas, lluvias intensas, sequía, vientos fuertes) con recomendaciones de acción.
- **Origen en el contexto:** Estrategia 1: alertas climáticas; reducir pérdidas por clima
- **Historias de usuario:** HU_010

### RF-07 · Alertas de plagas y enfermedades

- **Tipo:** Funcional
- **Prioridad:** Media
- **Descripción:** El sistema debe emitir alertas de riesgo de plagas y enfermedades relevantes para los cultivos registrados, con recomendaciones de manejo.
- **Origen en el contexto:** Encuesta: alertas de plagas y enfermedades como información más valiosa
- **Historias de usuario:** HU_011

### RF-08 · Consulta de precios de insumos

- **Tipo:** Funcional
- **Prioridad:** Media
- **Descripción:** El sistema debe permitir consultar precios actualizados de insumos agrícolas (fertilizantes, semillas, agroquímicos) por municipio o plaza de referencia.
- **Origen en el contexto:** Estrategia 3: precios de insumos y productos agrícolas
- **Historias de usuario:** HU_012

### RF-09 · Consulta de precios de productos agrícolas

- **Tipo:** Funcional
- **Prioridad:** Media
- **Descripción:** El sistema debe permitir consultar los precios de mercado de los productos agrícolas, su variación y su tendencia histórica, y configurar alertas de precio objetivo.
- **Origen en el contexto:** Estrategia 3; encuesta (57,1 % valora precios de mercado); dependencia de intermediarios
- **Historias de usuario:** HU_013, HU_014

### RF-10 · Funcionamiento sin conexión y sincronización

- **Tipo:** Funcional
- **Prioridad:** Alta
- **Descripción:** El sistema debe permitir consultar la información ya descargada y registrar datos sin conexión, y sincronizarlos automáticamente al recuperar la señal.
- **Origen en el contexto:** Estrategia 4: funciona con baja conectividad; encuesta (42,9 % reporta mala conectividad)
- **Historias de usuario:** HU_015, HU_016

### RF-11 · Notificaciones por WhatsApp y SMS

- **Tipo:** Funcional
- **Prioridad:** Media
- **Descripción:** El sistema debe permitir elegir el canal de las notificaciones (push, WhatsApp o SMS), los tipos de alerta y un horario silencioso.
- **Origen en el contexto:** Encuesta: 57,1 % prefiere WhatsApp como canal
- **Historias de usuario:** HU_018

### RF-12 · Capacitaciones

- **Tipo:** Funcional
- **Prioridad:** Media
- **Descripción:** El sistema debe ofrecer módulos de capacitación (video, guías) sobre el uso de la aplicación y prácticas agrícolas, con avance por módulo y descarga para ver sin conexión.
- **Origen en el contexto:** Encuesta: 92,9 % desea capacitaciones; alianzas con SENA
- **Historias de usuario:** HU_019

### RF-13 · Soporte técnico

- **Tipo:** Funcional
- **Prioridad:** Media
- **Descripción:** El sistema debe permitir al agricultor enviar consultas (con texto y fotos) a un asesor técnico y recibir respuesta dentro de la aplicación o por WhatsApp.
- **Origen en el contexto:** Integrar clima, precios y soporte técnico en un solo lugar; acompañamiento directo
- **Historias de usuario:** HU_020

### RF-14 · Planes y suscripción

- **Tipo:** Funcional
- **Prioridad:** Baja
- **Descripción:** El sistema debe ofrecer un plan gratuito y un plan de pago mensual (modelo freemium), permitiendo suscribirse, pagar y cancelar desde la aplicación.
- **Origen en el contexto:** Encuesta: 64,3 % pagaría entre $30.000 y $50.000 COP; estrategia freemium
- **Historias de usuario:** HU_021

### RF-15 · Panel de inicio

- **Tipo:** Funcional
- **Prioridad:** Media
- **Descripción:** El sistema debe mostrar en una sola pantalla el resumen del día: clima, alertas activas, estado de los cultivos y precio de referencia, con accesos directos a las demás funciones.
- **Origen en el contexto:** Centralizar la información en un solo lugar (57,1 % la considera muy útil)
- **Historias de usuario:** HU_022

### RNF-01 · Usabilidad para usuarios rurales

- **Tipo:** No funcional
- **Prioridad:** Alta
- **Descripción:** La interfaz debe ser simple: navegación de máximo 3 toques para cualquier función principal, textos grandes (mínimo 16 px), iconos con etiqueta y lenguaje cotidiano; un usuario nuevo debe poder registrar su primer cultivo sin ayuda en menos de 5 minutos.
- **Origen en el contexto:** Simplicidad de uso; 42,9 % desconoce el uso de apps
- **Historias de usuario:** HU_023

### RNF-02 · Bajo consumo de datos

- **Tipo:** No funcional
- **Prioridad:** Alta
- **Descripción:** La aplicación debe minimizar el consumo de datos: actualización incremental, compresión de imágenes, opción de solo Wi-Fi y de no descargar imágenes; el uso normal diario no debe superar 5 MB.
- **Origen en el contexto:** Plataforma ligera y de bajo consumo de datos
- **Historias de usuario:** HU_017

### RNF-03 · Rendimiento

- **Tipo:** No funcional
- **Prioridad:** Media
- **Descripción:** Las pantallas principales deben cargar en menos de 3 segundos con conexión 3G y en menos de 1 segundo con datos locales; la app no debe superar 50 MB de instalación.
- **Origen en el contexto:** Plataforma ligera; teléfonos de gama baja en zonas rurales
- **Historias de usuario:** HU_024

### RNF-04 · Compatibilidad multiplataforma

- **Tipo:** No funcional
- **Prioridad:** Alta
- **Descripción:** La aplicación debe funcionar en Android 8.0 o superior e iOS 14 o superior y estar disponible para descarga en Google Play y App Store.
- **Origen en el contexto:** Plaza: descarga en Google Play o App Store
- **Historias de usuario:** HU_025

### RNF-05 · Seguridad y protección de datos personales

- **Tipo:** No funcional
- **Prioridad:** Alta
- **Descripción:** El sistema debe cumplir la Ley 1581 de 2012 (habeas data): solicitar consentimiento informado, cifrar los datos en tránsito y en reposo, y permitir al usuario consultar, exportar y eliminar sus datos.
- **Origen en el contexto:** Contexto legal colombiano (PESTEL - Legal)
- **Historias de usuario:** HU_004

### RNF-06 · Confiabilidad de la información

- **Tipo:** No funcional
- **Prioridad:** Alta
- **Descripción:** Toda información de clima, alertas y precios debe indicar su fuente y fecha de actualización, y provenir de entidades reconocidas (p. ej. IDEAM, SIPSA-DANE); los datos desactualizados deben marcarse como tales.
- **Origen en el contexto:** Encuesta: 50 % desconfía de la información digital
- **Historias de usuario:** HU_026
