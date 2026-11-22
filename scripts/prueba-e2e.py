# -*- coding: utf-8 -*-
"""Recorrido de punta a punta contra el backend real (http://localhost:8080) y MongoDB."""
import json, sys, time, urllib.request, urllib.error, urllib.parse
from datetime import datetime, timedelta, timezone

BASE = "http://localhost:8080"
ok = fail = 0


def call(method, path, body=None, token=None):
    data = json.dumps(body).encode() if body is not None else None
    path = urllib.parse.quote(path, safe="/?&=%:+-_.")
    req = urllib.request.Request(BASE + path, data=data, method=method)
    req.add_header("Content-Type", "application/json")
    if token:
        req.add_header("Authorization", "Bearer " + token)
    try:
        with urllib.request.urlopen(req, timeout=30) as r:
            raw = r.read().decode()
            return r.status, (json.loads(raw) if raw else None)
    except urllib.error.HTTPError as e:
        raw = e.read().decode()
        try:
            return e.code, json.loads(raw)
        except Exception:
            return e.code, raw


def check(nombre, cond, extra=""):
    global ok, fail
    if cond:
        ok += 1
        print("  PASS", nombre)
    else:
        fail += 1
        print("  FAIL", nombre, extra)


def iso(dt):
    return dt.astimezone(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")


now = datetime.now(timezone.utc)
print("== 1. Usuario de demostración (Plus)")
s, r = call("POST", "/api/auth/login", {"celular": "3001234567", "password": "1234"})
check("login demo", s == 200 and r["usuario"]["plan"] == "PLUS", (s, r))
tok = r["token"]
s, r = call("GET", "/api/inicio", token=tok)
check("inicio con finca, clima y cultivos", s == 200 and r["tieneFinca"] and r["clima"] and len(r["cultivos"]) == 2 and r["precioReferencia"], (s, r))
s, r = call("GET", "/api/clima", token=tok)
check("clima Plus = 7 días", s == 200 and r["diasMostrados"] == 7 and len(r["dias"]) == 7 and not r["desactualizado"], (s, r))
s, r = call("GET", "/api/precios?categoria=INSUMO&plaza=Pasto", token=tok)
check("precios de insumos (5)", s == 200 and len(r) == 5 and all(i["categoria"] == "INSUMO" for i in r), (s, r))
s, r = call("GET", "/api/precios?categoria=PRODUCTO&q=cafe", token=tok)
check("búsqueda sin tildes 'cafe'", s == 200 and len(r) == 1 and r[0]["producto"] == "Café pergamino", (s, r))
s, r = call("GET", "/api/precios/tendencia?producto=Café pergamino&plaza=Pasto&dias=30".replace(" ", "%20"), token=tok)
check("tendencia 30 días (Plus)", s == 200 and len(r["puntos"]) == 30, (s, r if s != 200 else len(r["puntos"])))
s, r = call("GET", "/api/capacitaciones?cultivo=Café", token=tok)
check("capacitaciones de café incluyen generales", s == 200 and any(c.get("cultivo") is None for c in r) and any(c.get("cultivo") == "Café" for c in r), (s, r))
cap = r[0]["id"]
s, r = call("PUT", f"/api/capacitaciones/{cap}/avance", {"porcentaje": 50}, tok)
check("registrar avance de capacitación", s == 200 and r["avance"] == 50, (s, r))
s, r = call("POST", "/api/alertas/evaluar", token=tok)
check("evaluar alertas responde", s == 200 and "nuevas" in r, (s, r))
s, r2 = call("POST", "/api/alertas/evaluar", token=tok)
check("evaluar de nuevo no duplica alertas", s == 200 and r2["nuevas"] == 0, (s, r2))
s, lista = call("GET", "/api/alertas", token=tok)
check("listado de alertas", s == 200 and isinstance(lista, list), (s, lista))
print("     alertas generadas hoy:", [(a["tipo"], a["titulo"]) for a in lista][:5])

print("== 2. Agricultor nuevo (plan Gratis)")
cel = "31" + str(int(time.time()))[-8:]
s, r = call("POST", "/api/auth/registro", {"nombre": "Ana Prueba", "celular": cel, "password": "abcd", "aceptaDatos": True})
check("registro", s == 201 and r["usuario"]["plan"] == "GRATIS", (s, r))
t2 = r["token"]
s, r = call("POST", "/api/auth/registro", {"nombre": "Ana Prueba", "celular": cel, "password": "abcd", "aceptaDatos": True})
check("celular duplicado -> 409", s == 409, (s, r))
s, r = call("POST", "/api/auth/login", {"celular": cel, "password": "mala"})
check("contraseña incorrecta -> 401", s == 401, (s, r))
s, r = call("GET", "/api/clima", token=t2)
check("clima sin finca -> 400", s == 400 and "finca" in r["mensaje"].lower(), (s, r))
s, r = call("PUT", "/api/finca", {"nombre": "La Loma", "departamento": "Nariño", "municipio": "Ipiales", "vereda": "Yaramal"}, t2)
check("guardar finca (solo municipio)", s == 200 and r["municipio"] == "Ipiales", (s, r))
s, r = call("GET", "/api/clima", token=t2)
check("clima Gratis = 3 días", s == 200 and r["diasMostrados"] == 3 and len(r["dias"]) == 3, (s, r))
s, r = call("PUT", "/api/notificaciones/preferencias", {"canales": ["PUSH", "WHATSAPP"], "tipos": ["CLIMA"]}, t2)
check("WhatsApp requiere Plus -> 403", s == 403, (s, r))
s, r = call("POST", "/api/soporte/consultas", {"mensaje": "Hola"}, t2)
check("asesor requiere Plus -> 403", s == 403, (s, r))
s, r = call("GET", "/api/precios/tendencia?producto=Urea%2046%20%25&plaza=Pasto&dias=30", token=t2)
check("tendencia Gratis limitada a 7 días", s == 200 and len(r["puntos"]) == 7, (s, r if s != 200 else len(r["puntos"])))
s, r = call("POST", "/api/precios/alertas", {"producto": "Café pergamino", "plaza": "Pasto", "precioObjetivo": 1}, t2)
check("alerta de precio requiere Plus -> 403", s == 403, (s, r))
s, r = call("PUT", "/api/notificaciones/preferencias", {"canales": ["PUSH"], "tipos": ["CLIMA", "PLAGA", "LABOR"], "silencioInicio": "25:00"}, t2)
check("hora inválida -> 400", s == 400, (s, r))

print("== 3. Cultivos, actividades y sincronización")
s, c = call("POST", "/api/cultivos", {"tipo": "Café", "lote": "Lote A", "variedad": "Castillo", "areaHa": 0.5, "fechaSiembra": "2026-03-01"}, t2)
check("crear cultivo", s == 201 and c["estado"] == "ACTIVO", (s, c))
s, r = call("POST", "/api/cultivos", {"tipo": "", "lote": "", "fechaSiembra": None}, t2)
check("cultivo inválido -> 400 con detalles", s == 400 and "tipo" in r["detalles"], (s, r))
s, a = call("POST", f"/api/cultivos/{c['id']}/actividades", {"tipo": "RIEGO", "fecha": "2026-11-20", "nota": "2 h"}, t2)
check("crear actividad", s == 201, (s, a))
s, r = call("GET", f"/api/cultivos/{c['id']}/actividades", token=t2)
check("listar actividades", s == 200 and len(r) == 1, (s, r))
s, r = call("GET", f"/api/cultivos/{c['id']}", token=tok)
check("cultivo ajeno -> 404 (aislamiento entre usuarios)", s == 404, (s, r))
t_old = iso(now - timedelta(hours=2))
t_new = iso(now - timedelta(minutes=1))
sync = {"cultivos": [{"clienteId": "off-1", "tipo": "Papa", "lote": "Lote B", "areaHa": 0.3, "fechaSiembra": "2026-09-01", "actualizadoEn": t_old}],
        "actividades": [{"clienteId": "off-a1", "cultivoClienteId": "off-1", "tipo": "SIEMBRA", "fecha": "2026-09-01", "actualizadoEn": t_old}]}
s, r = call("POST", "/api/sync", sync, t2)
est = [x["estado"] for x in r["resultados"]] if s == 200 else r
check("sync crea cultivo y actividad (con referencia por clienteId)", s == 200 and est == ["CREADO", "CREADO"], (s, r))
s, r = call("POST", "/api/sync", sync, t2)
est = [x["estado"] for x in r["resultados"]] if s == 200 else r
check("sync repetido es idempotente (no duplica)", s == 200 and "CREADO" not in est, (s, r))
s, lc = call("GET", "/api/cultivos", token=t2)
check("hay 2 cultivos (sin duplicados)", s == 200 and len(lc) == 2, (s, lc))
upd = {"cultivos": [{"clienteId": "off-1", "tipo": "Papa", "lote": "Lote B EDITADO", "areaHa": 0.3, "fechaSiembra": "2026-09-01", "actualizadoEn": t_new}]}
s, r = call("POST", "/api/sync", upd, t2)
check("cambio más reciente gana (ACTUALIZADO)", s == 200 and r["resultados"][0]["estado"] == "ACTUALIZADO", (s, r))
stale = {"cultivos": [{"clienteId": "off-1", "tipo": "Papa", "lote": "VIEJO", "areaHa": 0.3, "fechaSiembra": "2026-09-01", "actualizadoEn": t_old}]}
s, r = call("POST", "/api/sync", stale, t2)
check("cambio más viejo se ignora (IGNORADO)", s == 200 and r["resultados"][0]["estado"] == "IGNORADO", (s, r))
s, ch = call("GET", "/api/sync/cambios?desde=" + iso(now - timedelta(days=1)), token=t2)
check("descarga incremental", s == 200 and len(ch["cultivos"]) >= 2, (s, ch))
s, r = call("PATCH", f"/api/cultivos/{c['id']}/estado?estado=COSECHADO", token=t2)
check("marcar cosechado", s == 200 and r["estado"] == "COSECHADO", (s, r))
s, r = call("GET", "/api/cultivos?estado=ACTIVO", token=t2)
check("filtro por estado", s == 200 and len(r) == 1, (s, r))

print("== 4. Suscripción, WhatsApp, asesor y alertas de precio")
s, r = call("POST", "/api/suscripcion", {"plan": "PLUS", "metodoPago": "TARJETA", "tokenPago": "RECHAZAR"}, t2)
check("pago rechazado -> 400", s == 400 and "rechazado" in r["mensaje"], (s, r))
s, r = call("POST", "/api/suscripcion", {"plan": "PLUS", "metodoPago": "NEQUI"}, t2)
check("pago aprobado -> Plus", s == 200 and r["plan"] == "PLUS", (s, r))
s, r = call("GET", "/api/clima", token=t2)
check("ahora clima de 7 días", s == 200 and r["diasMostrados"] == 7, (s, r))
s, r = call("PUT", "/api/notificaciones/preferencias", {"canales": ["PUSH", "WHATSAPP"], "tipos": ["CLIMA", "PLAGA", "PRECIO", "LABOR"], "numeroWhatsapp": "+57 300 111 2233"}, t2)
check("preferencias con WhatsApp (Plus)", s == 200 and r["numeroWhatsapp"] == "3001112233", (s, r))
s, q = call("POST", "/api/soporte/consultas", {"mensaje": "Mis hojas tienen manchas amarillas", "fotos": ["https://ejemplo.com/f1.jpg"]}, t2)
check("crear consulta al asesor", s == 201 and q["estado"] == "ENVIADA", (s, q))
s, r = call("GET", "/api/asesor/consultas", token=t2)
check("agricultor no ve la bandeja del asesor -> 403", s == 403, (s, r))
s, asesor = call("POST", "/api/auth/login", {"celular": "3000000001", "password": "asesor123"})
ta = asesor["token"]
s, r = call("GET", "/api/asesor/consultas", token=ta)
check("asesor ve consultas pendientes", s == 200 and any(x["id"] == q["id"] for x in r), (s, r))
s, r = call("POST", f"/api/asesor/consultas/{q['id']}/responder", {"respuesta": "Podría ser deficiencia de nitrógeno; aplica abono orgánico."}, ta)
check("asesor responde", s == 200 and r["estado"] == "RESPONDIDA", (s, r))
s, r = call("GET", f"/api/soporte/consultas/{q['id']}", token=t2)
check("agricultor ve la respuesta", s == 200 and r["respuesta"] and r["asesorNombre"], (s, r))
s, b = call("GET", "/api/notificaciones/bandeja", token=t2)
canales = {m["canal"] for m in b} if s == 200 else set()
check("bandeja: pago y respuesta llegaron por PUSH y WHATSAPP", {"PUSH", "WHATSAPP"} <= canales, (s, b))
s, ap = call("POST", "/api/precios/alertas", {"producto": "Café pergamino", "plaza": "Pasto", "precioObjetivo": 1000}, t2)
check("crear alerta de precio objetivo", s == 201, (s, ap))
s, adm = call("POST", "/api/auth/login", {"celular": "3000000000", "password": "admin123"})
tadm = adm["token"]
hoy = datetime.now().strftime("%Y-%m-%d")
s, r = call("POST", "/api/admin/precios", {"producto": "Café pergamino", "categoria": "PRODUCTO", "plaza": "Pasto", "unidad": "carga 125 kg", "precio": 2300000, "fecha": hoy, "fuente": "Carga manual"}, tadm)
check("admin carga precio", s == 200 and r["precio"] == 2300000, (s, r))
s, al = call("GET", "/api/alertas?tipo=PRECIO", token=t2)
check("se generó la alerta de precio", s == 200 and len(al) == 1, (s, al))
s, r = call("POST", "/api/admin/precios", {"producto": "x"}, t2)
check("agricultor no puede cargar precios -> 403", s == 403, (s, r))

print("== 5. Recordatorio programado (puede tardar hasta ~90 s)")
rec = iso(datetime.now(timezone.utc) - timedelta(seconds=5))
s, r = call("POST", f"/api/cultivos/{c['id']}/actividades", {"tipo": "FERTILIZACION", "fecha": hoy, "nota": "Fertilizar lote A", "recordatorio": rec}, t2)
check("actividad con recordatorio vencido", s == 201, (s, r))
s, pend = call("GET", "/api/actividades/pendientes", token=t2)
check("aparece como pendiente", s == 200 and len(pend) == 1, (s, pend))
alerta_labor = None
for _ in range(20):
    s, labor = call("GET", "/api/alertas?tipo=LABOR", token=t2)
    if s == 200 and labor:
        alerta_labor = labor[0]
        break
    time.sleep(5)
check("el scheduler convirtió el recordatorio en alerta LABOR", alerta_labor is not None)

print("== 6. Datos personales y recuperación")
s, r = call("POST", "/api/auth/recuperar", {"celular": cel})
check("recuperación devuelve código (modo desarrollo)", s == 200 and r.get("codigoDesarrollo"), (s, r))
code = r.get("codigoDesarrollo")
s, r = call("POST", "/api/auth/restablecer", {"celular": cel, "codigo": "000000", "nuevaPassword": "nueva1"})
check("código incorrecto -> 400", s == 400, (s, r))
s, r = call("POST", "/api/auth/restablecer", {"celular": cel, "codigo": code, "nuevaPassword": "nueva1"})
check("restablecer contraseña", s == 204, (s, r))
s, r = call("POST", "/api/auth/login", {"celular": cel, "password": "nueva1"})
check("login con la nueva contraseña", s == 200, (s, r))
t2 = r["token"]
s, ex = call("GET", "/api/me/datos", token=t2)
check("exportar datos personales", s == 200 and len(ex["cultivos"]) == 2 and ex["usuario"]["celular"] == cel and "passwordHash" not in json.dumps(ex), (s, ex if s != 200 else ""))
s, r = call("DELETE", "/api/me", token=t2)
check("eliminar cuenta", s == 204, (s, r))
s, r = call("POST", "/api/auth/login", {"celular": cel, "password": "nueva1"})
check("la cuenta eliminada ya no entra", s == 401, (s, r))

print(f"\nRESULTADO: {ok} correctas, {fail} fallidas")
sys.exit(1 if fail else 0)
