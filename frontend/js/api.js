/* Cliente de la API de AgroTec con modo sin conexión (RF-10, RNF-02).
   - Cada GET exitoso se guarda en localStorage; si no hay red, se muestra lo último guardado.
   - Los cultivos y actividades creados sin conexión van a una "bandeja de salida" y se envían
     en lote a /api/sync cuando vuelve la señal. */
const Api = (() => {
  const K_TOKEN = 'agrotec.token';
  const K_USER = 'agrotec.user';
  const K_OUTBOX = 'agrotec.outbox';

  const store = {
    get(k, def) { try { const v = localStorage.getItem(k); return v ? JSON.parse(v) : def; } catch (e) { return def; } },
    set(k, v) { try { localStorage.setItem(k, JSON.stringify(v)); } catch (e) { /* almacenamiento lleno o bloqueado */ } },
    del(k) { try { localStorage.removeItem(k); } catch (e) { /* nada */ } }
  };

  const api = {
    token: store.get(K_TOKEN, null),
    user: store.get(K_USER, null),
    offline: false,   // true si la última lectura salió del almacenamiento local
    cachedAt: null,
    onLogout: null
  };

  // ---------- sesión ----------
  api.setSession = (token, user) => {
    api.token = token; api.user = user;
    store.set(K_TOKEN, token); store.set(K_USER, user);
  };
  api.setUser = (user) => { api.user = user; store.set(K_USER, user); };
  api.logout = () => {
    api.token = null; api.user = null;
    store.del(K_TOKEN); store.del(K_USER);
    if (api.onLogout) api.onLogout();
  };

  // ---------- HTTP ----------
  async function request(method, path, body) {
    const headers = { 'Content-Type': 'application/json' };
    if (api.token) headers.Authorization = 'Bearer ' + api.token;
    let res;
    try {
      res = await fetch(path, { method, headers, body: body === undefined ? undefined : JSON.stringify(body) });
    } catch (e) {
      const err = new Error('Sin conexión a internet');
      err.offline = true;
      throw err;
    }
    if (res.status === 204) return null;
    let data = null;
    try { data = await res.json(); } catch (e) { /* respuesta sin cuerpo */ }
    if (!res.ok) {
      if (res.status === 401 && !path.startsWith('/api/auth/')) api.logout();
      const err = new Error((data && data.mensaje) || 'Ocurrió un error. Intenta de nuevo.');
      err.status = res.status;
      err.detalles = data && data.detalles;
      throw err;
    }
    return data;
  }

  // ---------- caché de lecturas ----------
  const claveCache = (path) => 'agrotec.c.' + (api.user ? api.user.id : 'anon') + '.' + path;

  api.get = async (path) => {
    try {
      const data = await request('GET', path);
      store.set(claveCache(path), { at: new Date().toISOString(), data });
      api.offline = false;
      return data;
    } catch (e) {
      if (e.offline) {
        const c = store.get(claveCache(path), null);
        if (c) { api.offline = true; api.cachedAt = c.at; return c.data; }
      }
      throw e;
    }
  };
  api.post = (path, body) => request('POST', path, body === undefined ? {} : body);
  api.put = (path, body) => request('PUT', path, body);
  api.patch = (path, body) => request('PATCH', path, body);
  api.del = (path) => request('DELETE', path);

  // ---------- bandeja de salida (sin conexión) ----------
  const outbox = () => store.get(K_OUTBOX, { cultivos: [], actividades: [] });
  const guardarOutbox = (o) => store.set(K_OUTBOX, o);
  const nuevoId = () => (self.crypto && crypto.randomUUID ? crypto.randomUUID()
    : 'c-' + Date.now() + '-' + Math.random().toString(16).slice(2));

  api.pendientes = () => { const o = outbox(); return o.cultivos.length + o.actividades.length; };
  api.cultivosPendientes = () => outbox().cultivos;
  api.actividadesPendientes = (cultivoId) => outbox().actividades.filter((a) => a.cultivoId === cultivoId);

  api.encolarCultivo = (c) => {
    const o = outbox();
    const item = Object.assign({ clienteId: nuevoId(), estado: 'ACTIVO', actualizadoEn: new Date().toISOString() }, c);
    o.cultivos.push(item); guardarOutbox(o); return item;
  };
  api.encolarActividad = (a) => {
    const o = outbox();
    const item = Object.assign({ clienteId: nuevoId(), completada: false, actualizadoEn: new Date().toISOString() }, a);
    o.actividades.push(item); guardarOutbox(o); return item;
  };

  /** Envía todo lo pendiente. Devuelve el resumen del servidor o null si no había nada. */
  api.sincronizar = async () => {
    const o = outbox();
    if (!o.cultivos.length && !o.actividades.length) return null;
    const res = await request('POST', '/api/sync', { cultivos: o.cultivos, actividades: o.actividades });
    guardarOutbox({ cultivos: [], actividades: [] });
    return res;
  };

  return api;
})();
