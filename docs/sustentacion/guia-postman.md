# Postman desde cero

## Qué es
Un programa para **enviar peticiones al backend sin usar pantallas** y ver qué responde. Demuestra que la API funciona sola.

Una petición tiene: **método** (GET consultar, POST crear, PUT reemplazar, PATCH cambiar algo, DELETE borrar), **dirección** (URL), **encabezados** (aquí va el token), **cuerpo** en JSON (los datos). La respuesta trae un **código** (200, 201, 400…) y un JSON.

## Instalar e importar
1. Descarga Postman desde https://www.postman.com/downloads/ e instálalo (pide una cuenta gratuita; la creas tú).
2. Arranca la app (ver [guía de ejecución](../guia-ejecucion.md)); necesitas `SEED_DEMO_USERS=true`, que es el valor por defecto.
3. En Postman: **Import** → elige el archivo `postman/AgroTec.postman_collection.json` del proyecto.

## Primera prueba (hazla a mano)
1. Abre **01 Autenticación → Login agricultor** y pulsa **Send**. Debe salir **200** y un `token`.
2. Abre **03 Cultivos → Registrar cultivo** → **Send**. Debe salir **201**.
3. Abre **Listar cultivos** → **Send**: ahí está.
4. Abre **Datos inválidos (400)** y lee el mensaje de error.

## Cómo viaja el token
El login guarda el token en una **variable de la colección** (pestaña *Scripts* → *Post-response*). Las demás peticiones envían el encabezado `Authorization: Bearer {{token}}`. Si quitas ese encabezado, recibes **401**.

## Ejecutar todo
Clic derecho sobre **AgroTec API** → **Run** → **Run AgroTec API**. Ejecuta 64 peticiones en orden y muestra en verde las que responden lo esperado. **Hazlo en orden**: unas usan datos que crean las anteriores.

## Qué te pueden pedir
- «Cambia un dato y envíalo»: edita el JSON en **Body → raw**.
- «Crea una petición nueva»: **+**, elige el método, escribe `{{baseUrl}}/api/...`, agrega el encabezado `Authorization`.
- «Muéstrame un error»: quita un campo obligatorio (400), quita el token (401), usa un token de agricultor en `/api/admin/precios` (403), pide un id inexistente (404).

## Alternativa sin Postman
http://localhost:8080/swagger-ui.html muestra todos los endpoints y permite probarlos desde el navegador.

Para qué sirve cada petición está escrito en su pestaña **Docs/Description**, con la historia de usuario y la clase que la atiende.
