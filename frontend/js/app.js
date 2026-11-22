/* AgroTec — aplicación web móvil (SPA con rutas por hash). Sin dependencias externas. */
'use strict';

const $app = document.getElementById('app');
const $toast = document.getElementById('toast');

// ====================== utilidades ======================
const esc = (s) => String(s == null ? '' : s).replace(/[&<>"']/g, (c) =>
  ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const cop = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 });
const fecha = (iso) => { if (!iso) return ''; const [y, m, d] = String(iso).slice(0, 10).split('-'); return d + '/' + m + '/' + y; };
const hora = (iso) => new Date(iso).toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' });
const fechaHora = (iso) => fecha(iso) + ' ' + hora(iso);
const hoyISO = () => { const d = new Date(); return new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 10); };
const $ = (sel, raiz) => (raiz || document).querySelector(sel);
const $$ = (sel, raiz) => Array.from((raiz || document).querySelectorAll(sel));

let toastTimer = null;
function toast(msg) {
  $toast.textContent = msg;
  $toast.classList.add('ver');
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => $toast.classList.remove('ver'), 3200);
}

const TIPOS_ACTIVIDAD = {
  SIEMBRA: 'Siembra', RIEGO: 'Riego', FERTILIZACION: 'Fertilización', CONTROL_PLAGAS: 'Control de plagas',
  DESHIERBA: 'Deshierba', COSECHA: 'Cosecha', OTRA: 'Otra'
};
const iconoClima = (d) => ({ Despejado: '☀️', Nublado: '☁️', Lluvioso: '🌧️' }[d] || '⛅');
const pastillaNivel = (n) => n === 'ALTO' ? '<span class="pastilla roja">Riesgo alto</span>'
  : n === 'MEDIO' ? '<span class="pastilla naranja">Riesgo medio</span>' : '<span class="pastilla">Aviso</span>';
const iconoAlerta = (t) => ({ CLIMA: '🌦️', PLAGA: '🐛', PRECIO: '💲', LABOR: '⏰' }[t] || '🔔');
const flecha = (t) => t === 'SUBE' ? '<span class="sube">▲</span>' : t === 'BAJA' ? '<span class="baja">▼</span>' : '<span class="estable">▬</span>';
const esPlus = () => Api.user && Api.user.plan === 'PLUS';

/** Dibuja una pantalla completa: encabezado, avisos de conexión, contenido y barra inferior. */
function pantalla(titulo, cuerpo, op) {
  const o = op || {};
  const pend = Api.pendientes();
  const avisoOffline = Api.offline
    ? '<div class="banner">📴 Sin conexión · datos guardados' + (Api.cachedAt ? ' (' + esc(fechaHora(Api.cachedAt)) + ')' : '') + '</div>' : '';
  const avisoPend = pend
    ? '<div class="banner info">⏳ ' + pend + ' cambio(s) por sincronizar <button id="btn-sync">Sincronizar</button></div>' : '';
  const nav = o.nav ? navegacion(o.nav) : '';
  $app.innerHTML =
    '<header class="top">' + (o.atras ? '<a class="back" href="' + o.atras + '" aria-label="Volver">←</a>' : '') +
    '<h1>' + titulo + '</h1>' + (o.accion || '') + '</header>' + avisoOffline + avisoPend +
    '<main>' + cuerpo + '</main>' + nav + (o.fab || '');
  const b = $('#btn-sync');
  if (b) b.onclick = () => sincronizarAhora(true);
  window.scrollTo(0, 0);
}

function navegacion(activa) {
  const items = [['inicio', '🏠', 'Inicio'], ['cultivos', '🌱', 'Cultivos'], ['clima', '⛅', 'Clima'], ['precios', '💲', 'Precios'], ['mas', '☰', 'Más']];
  return '<nav class="bottom" aria-label="Principal">' + items.map((i) =>
    '<a href="#/' + i[0] + '" class="' + (i[0] === activa ? 'on' : '') + '"' + (i[0] === activa ? ' aria-current="page"' : '') +
    '><span aria-hidden="true">' + i[1] + '</span>' + i[2] + '</a>').join('') + '</nav>';
}

function errorCard(e, reintentar) {
  const sinFinca = /finca/i.test(e.message || '');
  return '<div class="card"><p class="error">' + esc(e.message || 'Ocurrió un error') + '</p>' +
    (sinFinca ? '<a class="btn full" href="#/finca">Registrar mi finca</a>'
      : (reintentar ? '<button class="sec full" id="reintentar">Reintentar</button>' : '')) + '</div>';
}

function mostrarError(el, e) {
  let txt = esc(e.message || 'Ocurrió un error');
  if (e.detalles) txt += '<br>' + Object.values(e.detalles).map(esc).join('<br>');
  el.innerHTML = '<div class="error" role="alert">' + txt + '</div>';
}

async function sincronizarAhora(avisar) {
  try {
    const r = await Api.sincronizar();
    if (r) {
      const errores = r.resultados.filter((x) => x.estado === 'ERROR').length;
      toast(errores ? 'Sincronizado con ' + errores + ' error(es)' : '✔ Todo al día');
      render();
    } else if (avisar) toast('No hay nada por sincronizar');
  } catch (e) {
    if (avisar) toast(e.offline ? 'Todavía sin conexión' : e.message);
  }
}

// ====================== rutas ======================
const RUTAS = [
  [/^#\/login$/, () => vistaAuth(), true],
  [/^#\/inicio$/, () => vistaInicio()],
  [/^#\/cultivos$/, () => vistaCultivos()],
  [/^#\/cultivos\/nuevo$/, () => vistaCultivoForm(null)],
  [/^#\/cultivos\/([^/]+)\/editar$/, (m) => vistaCultivoForm(m[1])],
  [/^#\/cultivos\/([^/]+)$/, (m) => vistaCultivo(m[1])],
  [/^#\/clima$/, () => vistaClima()],
  [/^#\/alertas$/, () => vistaAlertas()],
  [/^#\/precios$/, () => vistaPrecios()],
  [/^#\/mas$/, () => vistaMas()],
  [/^#\/finca$/, () => vistaFinca()],
  [/^#\/notificaciones$/, () => vistaNotificaciones()],
  [/^#\/capacitaciones$/, () => vistaCapacitaciones()],
  [/^#\/soporte$/, () => vistaSoporte()],
  [/^#\/planes$/, () => vistaPlanes()],
  [/^#\/datos$/, () => vistaDatos()]
];

async function render() {
  const hash = location.hash || '#/inicio';
  const ruta = RUTAS.find((r) => r[0].test(hash));
  if (!ruta) { location.hash = '#/inicio'; return; }
  if (!ruta[2] && !Api.token) { location.hash = '#/login'; return; }
  if (ruta[2] && Api.token) { location.hash = '#/inicio'; return; }
  try {
    await ruta[1](hash.match(ruta[0]));
  } catch (e) {
    if (!Api.token && !ruta[2]) return; // la sesión se cerró (401)
    pantalla('AgroTec', errorCard(e, true), { nav: 'inicio' });
    const r = $('#reintentar');
    if (r) r.onclick = render;
  }
}

Api.onLogout = () => { location.hash = '#/login'; };
window.addEventListener('hashchange', render);
window.addEventListener('online', () => { sincronizarAhora(false); });

// ====================== autenticación ======================
function vistaAuth() {
  let modo = 'login';
  let celularRec = '';

  const pintar = () => {
    const tabs = modo === 'login' || modo === 'registro'
      ? '<div class="tabs"><button data-m="login" class="' + (modo === 'login' ? 'on' : '') + '">Iniciar sesión</button>' +
        '<button data-m="registro" class="' + (modo === 'registro' ? 'on' : '') + '">Crear cuenta</button></div>' : '';
    $app.innerHTML = '<div class="hero"><img src="icon.svg" alt=""><h1>AgroTec</h1><p class="suave">Tu asistente para el campo</p></div>' +
      '<main>' + tabs + '<form id="f" class="card" novalidate>' + formulario() + '</form></main>';
    $$('.tabs button').forEach((b) => b.onclick = () => { modo = b.dataset.m; pintar(); });
    const f = $('#f');
    f.onsubmit = enviar;
    const olv = $('#olvide'); if (olv) olv.onclick = () => { modo = 'recuperar'; pintar(); };
    const vol = $('#volver'); if (vol) vol.onclick = () => { modo = 'login'; pintar(); };
  };

  const formulario = () => {
    if (modo === 'login') return '' +
      '<label for="cel">Número de celular</label><input id="cel" type="tel" inputmode="numeric" autocomplete="username" placeholder="300 000 0000" required>' +
      '<label for="pw">Contraseña o PIN</label><input id="pw" type="password" autocomplete="current-password" required>' +
      '<div id="msg"></div><button class="full" style="margin-top:12px">Entrar</button>' +
      '<p class="centro" style="margin-top:12px"><a href="#" id="olvide">¿Olvidaste tu contraseña?</a></p>';
    if (modo === 'registro') return '' +
      '<label for="nom">Nombre completo</label><input id="nom" autocomplete="name" placeholder="Ej. David Mateo Torres" required>' +
      '<label for="cel">Número de celular</label><input id="cel" type="tel" inputmode="numeric" autocomplete="username" placeholder="300 000 0000" required>' +
      '<label for="pw">Contraseña o PIN (mínimo 4)</label><input id="pw" type="password" autocomplete="new-password" required>' +
      '<label class="check"><input id="ok" type="checkbox"> Acepto el tratamiento de mis datos personales (Ley 1581 de 2012)</label>' +
      '<div id="msg"></div><button class="full" style="margin-top:8px">Crear mi cuenta</button>';
    if (modo === 'recuperar') return '' +
      '<h3>Recuperar contraseña</h3><p class="suave">Te enviaremos un código por SMS.</p>' +
      '<label for="cel">Número de celular</label><input id="cel" type="tel" inputmode="numeric" placeholder="300 000 0000" required>' +
      '<div id="msg"></div><button class="full" style="margin-top:12px">Enviar código</button>' +
      '<p class="centro" style="margin-top:12px"><a href="#" id="volver">Volver</a></p>';
    return '' +
      '<h3>Nueva contraseña</h3><div id="info"></div>' +
      '<label for="cod">Código recibido</label><input id="cod" inputmode="numeric" autocomplete="one-time-code" required>' +
      '<label for="pw">Nueva contraseña o PIN</label><input id="pw" type="password" autocomplete="new-password" required>' +
      '<div id="msg"></div><button class="full" style="margin-top:12px">Cambiar contraseña</button>' +
      '<p class="centro" style="margin-top:12px"><a href="#" id="volver">Volver</a></p>';
  };

  const enviar = async (ev) => {
    ev.preventDefault();
    const msg = $('#msg');
    const btn = $('#f button:not([type=button])');
    msg.innerHTML = '';
    btn.disabled = true;
    try {
      if (modo === 'login') {
        const r = await Api.post('/api/auth/login', { celular: $('#cel').value, password: $('#pw').value });
        Api.setSession(r.token, r.usuario);
        sincronizarAhora(false);
        location.hash = '#/inicio';
      } else if (modo === 'registro') {
        const r = await Api.post('/api/auth/registro', {
          nombre: $('#nom').value, celular: $('#cel').value, password: $('#pw').value, aceptaDatos: $('#ok').checked
        });
        Api.setSession(r.token, r.usuario);
        toast('¡Cuenta creada! Cuéntanos dónde está tu finca.');
        location.hash = '#/finca';
      } else if (modo === 'recuperar') {
        celularRec = $('#cel').value;
        const r = await Api.post('/api/auth/recuperar', { celular: celularRec });
        modo = 'restablecer';
        pintar();
        $('#info').innerHTML = '<div class="ok">' + esc(r.mensaje) + '</div>' +
          (r.codigoDesarrollo ? '<p class="suave">Modo desarrollo — tu código es <strong>' + esc(r.codigoDesarrollo) + '</strong></p>' : '');
      } else {
        await Api.post('/api/auth/restablecer', { celular: celularRec, codigo: $('#cod').value, nuevaPassword: $('#pw').value });
        toast('Contraseña actualizada. Ya puedes iniciar sesión.');
        modo = 'login'; pintar();
      }
    } catch (e) {
      mostrarError(msg, e);
      btn.disabled = false;
    }
  };
  pintar();
}

// ====================== inicio ======================
async function vistaInicio() {
  const d = await Api.get('/api/inicio');
  Api.setUser(Object.assign({}, Api.user, { plan: d.plan }));
  let h = '<p class="suave" style="margin:0">Hola, <strong>' + esc((d.nombre || '').split(' ')[0]) + '</strong> 👋</p>';

  if (!d.tieneFinca) {
    h += '<div class="card"><h3>Registra tu finca</h3><p class="suave">Así te mostramos el clima y las alertas de tu zona.</p><a class="btn full" href="#/finca">Registrar mi finca</a></div>';
  } else if (d.clima) {
    h += '<div class="card"><div class="fila"><div class="grow"><p class="suave">Clima hoy · ' + esc(d.ubicacion) + '</p>' +
      '<div class="num">' + Math.round(d.clima.temperatura) + ' °C</div>' +
      '<p class="suave">' + esc(d.clima.descripcion) + ' · humedad ' + d.clima.humedad + ' %</p></div>' +
      '<div style="font-size:3rem" aria-hidden="true">' + iconoClima(d.clima.descripcion) + '</div></div>' +
      '<a href="#/clima">Ver pronóstico →</a></div>';
  }

  if (d.ultimaAlerta) {
    const a = d.ultimaAlerta;
    h += '<a class="card fila-link alerta-' + esc(a.nivel) + '" href="#/alertas"><div class="fila"><span style="font-size:1.8rem">' + iconoAlerta(a.tipo) + '</span>' +
      '<div class="grow"><h3>' + esc(a.titulo) + '</h3><p class="suave">' + esc(a.mensaje) + '</p></div></div>' +
      (d.alertasSinLeer ? '<p class="suave" style="margin-top:6px">' + d.alertasSinLeer + ' alerta(s) sin leer</p>' : '') + '</a>';
  }

  h += '<div class="fila"><h2 class="grow">Mis cultivos</h2><a href="#/cultivos">Ver todos</a></div>';
  if (d.cultivos && d.cultivos.length) {
    h += '<div class="lista">' + d.cultivos.map((c) =>
      '<a class="card fila-link" href="#/cultivos/' + esc(c.id) + '"><div class="fila"><span style="font-size:1.6rem">🌱</span><div class="grow"><h3>' +
      esc(c.tipo) + ' · ' + esc(c.lote) + '</h3><p class="suave">Siembra: ' + fecha(c.fechaSiembra) + '</p></div><span class="pastilla">Activo</span></div></a>').join('') + '</div>';
  } else {
    h += '<div class="card vacio">Aún no tienes cultivos.<br><a class="btn" style="margin-top:10px" href="#/cultivos/nuevo">Registrar mi primer cultivo</a></div>';
  }

  if (d.precioReferencia) {
    const p = d.precioReferencia;
    h += '<a class="card fila-link" href="#/precios"><div class="fila"><div class="grow"><p class="suave">Precio hoy · ' + esc(p.plaza) + '</p><h3>' + esc(p.producto) +
      '</h3></div><div><strong>' + cop.format(p.precio) + '</strong> ' + flecha(p.tendencia) + '<div class="suave">' + esc(p.unidad) + '</div></div></div></a>';
  }

  h += '<h2>Atajos</h2><div class="grid4">' +
    '<a class="atajo" href="#/clima"><span>⛅</span>Clima</a><a class="atajo" href="#/precios"><span>💲</span>Precios</a>' +
    '<a class="atajo" href="#/capacitaciones"><span>🎓</span>Capacitación</a><a class="atajo" href="#/soporte"><span>👩‍🌾</span>Asesor</a></div>';

  pantalla('AgroTec', h, { nav: 'inicio', accion: '<a class="accion" href="#/alertas">🔔' + (d.alertasSinLeer ? ' ' + d.alertasSinLeer : '') + '</a>' });
}

// ====================== cultivos ======================
let filtroCultivos = 'TODOS';
let busquedaCultivos = '';

async function vistaCultivos() {
  const servidor = await Api.get('/api/cultivos');
  const locales = Api.cultivosPendientes().map((c) => Object.assign({}, c, { id: null, pendiente: true, estado: c.estado || 'ACTIVO' }));
  const todos = locales.concat(servidor);

  const pintarLista = () => {
    const q = busquedaCultivos.trim().toLowerCase();
    const lista = todos.filter((c) => (filtroCultivos === 'TODOS' || c.estado === filtroCultivos) &&
      (!q || [c.tipo, c.lote, c.variedad].join(' ').toLowerCase().includes(q)));
    $('#lista').innerHTML = lista.length ? lista.map((c) => {
      const cuerpo = '<div class="fila"><span style="font-size:1.6rem">🌱</span><div class="grow"><h3>' + esc(c.tipo) + ' · ' + esc(c.lote) + '</h3>' +
        '<p class="suave">' + (c.areaHa ? esc(c.areaHa) + ' ha · ' : '') + esc(c.variedad || 'Sin variedad') + '</p></div>' +
        (c.pendiente ? '<span class="pastilla gris">Pendiente</span>' : '<span class="pastilla ' + (c.estado === 'ACTIVO' ? '' : 'gris') + '">' + (c.estado === 'ACTIVO' ? 'Activo' : 'Cosechado') + '</span>') + '</div>';
      return c.pendiente ? '<div class="card">' + cuerpo + '<p class="suave">Se enviará cuando haya conexión.</p></div>'
        : '<a class="card fila-link" href="#/cultivos/' + esc(c.id) + '">' + cuerpo + '</a>';
    }).join('') : '<div class="card vacio">No hay cultivos que mostrar.</div>';
  };

  pantalla('Mis cultivos',
    '<input id="q" type="search" placeholder="🔍 Buscar cultivo" value="' + esc(busquedaCultivos) + '" aria-label="Buscar cultivo">' +
    '<div class="chips">' + [['TODOS', 'Todos'], ['ACTIVO', 'Activos'], ['COSECHADO', 'Cosechados']].map((f) =>
      '<button class="chip ' + (filtroCultivos === f[0] ? 'on' : '') + '" data-f="' + f[0] + '">' + f[1] + '</button>').join('') + '</div>' +
    '<div id="lista" class="lista"></div>',
    { nav: 'cultivos', fab: '<a class="btn fab" href="#/cultivos/nuevo" aria-label="Agregar cultivo">+</a>' });
  pintarLista();
  $('#q').oninput = (e) => { busquedaCultivos = e.target.value; pintarLista(); };
  $$('.chip').forEach((b) => b.onclick = () => { filtroCultivos = b.dataset.f; $$('.chip').forEach((x) => x.classList.toggle('on', x === b)); pintarLista(); });
}

async function vistaCultivoForm(id) {
  const c = id ? await Api.get('/api/cultivos/' + id) : { fechaSiembra: hoyISO() };
  pantalla(id ? 'Editar cultivo' : 'Nuevo cultivo',
    '<form id="f" class="card" novalidate>' +
    '<label for="tipo">Tipo de cultivo</label><input id="tipo" list="tipos" value="' + esc(c.tipo) + '" placeholder="Ej. Café" required>' +
    '<datalist id="tipos"><option>Café</option><option>Papa</option><option>Maíz</option><option>Fríjol</option><option>Arveja</option></datalist>' +
    '<label for="lote">Nombre del lote</label><input id="lote" value="' + esc(c.lote) + '" placeholder="Ej. Lote 1" required>' +
    '<label for="variedad">Variedad</label><input id="variedad" value="' + esc(c.variedad) + '" placeholder="Ej. Castillo">' +
    '<label for="area">Área sembrada (hectáreas)</label><input id="area" type="number" step="0.01" min="0" inputmode="decimal" value="' + esc(c.areaHa) + '" placeholder="0,5">' +
    '<div class="dos"><div><label for="siembra">Fecha de siembra</label><input id="siembra" type="date" value="' + esc(c.fechaSiembra) + '" required></div>' +
    '<div><label for="cosecha">Cosecha estimada</label><input id="cosecha" type="date" value="' + esc(c.fechaCosechaEstimada) + '"></div></div>' +
    '<label for="notas">Notas (opcional)</label><textarea id="notas" rows="3">' + esc(c.notas) + '</textarea>' +
    '<div id="msg"></div><div class="fila-botones" style="margin-top:12px"><a class="btn sec" href="' + (id ? '#/cultivos/' + id : '#/cultivos') + '">Cancelar</a><button>Guardar cultivo</button></div></form>',
    { atras: id ? '#/cultivos/' + id : '#/cultivos' });
  $('#f').onsubmit = async (ev) => {
    ev.preventDefault();
    const cuerpo = {
      tipo: $('#tipo').value, lote: $('#lote').value, variedad: $('#variedad').value || null,
      areaHa: $('#area').value ? Number($('#area').value) : null, fechaSiembra: $('#siembra').value || null,
      fechaCosechaEstimada: $('#cosecha').value || null, notas: $('#notas').value || null
    };
    try {
      if (id) { await Api.put('/api/cultivos/' + id, cuerpo); toast('Cultivo actualizado'); location.hash = '#/cultivos/' + id; return; }
      try { await Api.post('/api/cultivos', cuerpo); toast('Cultivo guardado'); }
      catch (e) {
        if (!e.offline) throw e;
        Api.encolarCultivo(cuerpo);
        toast('Sin conexión: se guardó y se enviará cuando haya señal');
      }
      location.hash = '#/cultivos';
    } catch (e) { mostrarError($('#msg'), e.offline ? new Error('Necesitas conexión para editar un cultivo') : e); }
  };
}

async function vistaCultivo(id) {
  const [c, acts] = await Promise.all([Api.get('/api/cultivos/' + id), Api.get('/api/cultivos/' + id + '/actividades')]);
  const pend = Api.actividadesPendientes(id).map((a) => Object.assign({}, a, { id: null, pendiente: true }));
  const lista = pend.concat(acts);

  const filaAct = (a) => '<div class="card"><div class="fila"><div class="grow"><h3>' + esc(TIPOS_ACTIVIDAD[a.tipo] || a.tipo) + (a.completada ? ' ✔' : '') + '</h3>' +
    '<p class="suave">' + fecha(a.fecha) + (a.nota ? ' · ' + esc(a.nota) : '') + '</p>' +
    (a.recordatorio ? '<p class="suave">🔔 ' + fechaHora(a.recordatorio) + '</p>' : '') +
    (a.pendiente ? '<span class="pastilla gris">Pendiente de enviar</span>' : '') + '</div>' +
    (a.pendiente ? '' : (!a.completada && a.recordatorio ? '<button class="chico sec" data-ok="' + esc(a.id) + '">Hecha</button>' : '') +
      '<button class="chico peligro" data-del="' + esc(a.id) + '" aria-label="Eliminar actividad">🗑</button>') + '</div></div>';

  pantalla(esc(c.tipo) + ' · ' + esc(c.lote),
    '<div class="card"><div class="fila"><div class="grow"><p class="suave">' + (c.areaHa ? esc(c.areaHa) + ' ha · ' : '') + esc(c.variedad || 'Sin variedad') + '</p>' +
    '<p>Siembra: <strong>' + fecha(c.fechaSiembra) + '</strong></p>' + (c.fechaCosechaEstimada ? '<p>Cosecha estimada: <strong>' + fecha(c.fechaCosechaEstimada) + '</strong></p>' : '') +
    (c.notas ? '<p class="suave">' + esc(c.notas) + '</p>' : '') + '</div><span class="pastilla ' + (c.estado === 'ACTIVO' ? '' : 'gris') + '">' + (c.estado === 'ACTIVO' ? 'Activo' : 'Cosechado') + '</span></div>' +
    '<div class="fila-botones" style="margin-top:12px"><a class="btn sec" href="#/cultivos/' + esc(id) + '/editar">Editar</a>' +
    '<button class="sec" id="estado">' + (c.estado === 'ACTIVO' ? 'Marcar cosechado' : 'Reactivar') + '</button>' +
    '<button class="peligro" id="borrar">Eliminar</button></div></div>' +
    '<h2>Actividades</h2><form id="fa" class="card" novalidate>' +
    '<div class="dos"><div><label for="tipo">Actividad</label><select id="tipo">' + Object.keys(TIPOS_ACTIVIDAD).map((k) => '<option value="' + k + '">' + TIPOS_ACTIVIDAD[k] + '</option>').join('') + '</select></div>' +
    '<div><label for="fecha">Fecha</label><input id="fecha" type="date" value="' + hoyISO() + '"></div></div>' +
    '<label for="nota">Nota (opcional)</label><input id="nota" placeholder="Ej. 15 g por planta">' +
    '<label for="rec">Recordatorio (opcional)</label><input id="rec" type="datetime-local">' +
    '<div id="msg"></div><button class="full" style="margin-top:10px">Registrar actividad</button></form>' +
    (lista.length ? '<div class="lista">' + lista.map(filaAct).join('') + '</div>' : '<div class="vacio">Aún no hay actividades registradas.</div>'),
    { atras: '#/cultivos', nav: 'cultivos' });

  $('#fa').onsubmit = async (ev) => {
    ev.preventDefault();
    const cuerpo = {
      tipo: $('#tipo').value, fecha: $('#fecha').value, nota: $('#nota').value || null,
      recordatorio: $('#rec').value ? new Date($('#rec').value).toISOString() : null
    };
    try {
      try { await Api.post('/api/cultivos/' + id + '/actividades', cuerpo); toast('Actividad registrada'); }
      catch (e) {
        if (!e.offline) throw e;
        Api.encolarActividad(Object.assign({ cultivoId: id }, cuerpo));
        toast('Sin conexión: se enviará cuando haya señal');
      }
      render();
    } catch (e) { mostrarError($('#msg'), e); }
  };
  $$('[data-ok]').forEach((b) => b.onclick = async () => { try { await Api.patch('/api/actividades/' + b.dataset.ok + '/completar'); toast('¡Hecha!'); render(); } catch (e) { toast(e.offline ? 'Necesitas conexión' : e.message); } });
  $$('[data-del]').forEach((b) => b.onclick = async () => {
    if (!confirm('¿Eliminar esta actividad?')) return;
    try { await Api.del('/api/actividades/' + b.dataset.del); render(); } catch (e) { toast(e.offline ? 'Necesitas conexión' : e.message); }
  });
  $('#estado').onclick = async () => {
    try { await Api.patch('/api/cultivos/' + id + '/estado?estado=' + (c.estado === 'ACTIVO' ? 'COSECHADO' : 'ACTIVO')); render(); }
    catch (e) { toast(e.offline ? 'Necesitas conexión' : e.message); }
  };
  $('#borrar').onclick = async () => {
    if (!confirm('¿Eliminar este cultivo y todas sus actividades? Esta acción no se puede deshacer.')) return;
    try { await Api.del('/api/cultivos/' + id); toast('Cultivo eliminado'); location.hash = '#/cultivos'; }
    catch (e) { toast(e.offline ? 'Necesitas conexión' : e.message); }
  };
}

// ====================== clima ======================
const NOMBRE_DIA = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];

async function vistaClima() {
  let h;
  try {
    const c = await Api.get('/api/clima');
    h = '<p class="suave" style="margin:0">📍 ' + esc(c.ubicacion) + '</p>' +
      '<div class="card"><div class="fila"><div class="grow"><div class="num">' + Math.round(c.actual.temperatura) + ' °C</div>' +
      '<p class="suave">' + esc(c.actual.descripcion) + ' · humedad ' + c.actual.humedad + ' %</p><p class="suave">Viento ' + Math.round(c.actual.vientoKmh) + ' km/h</p></div>' +
      '<div style="font-size:3.4rem" aria-hidden="true">' + iconoClima(c.actual.descripcion) + '</div></div></div>' +
      '<h2>Próximos ' + c.diasMostrados + ' días</h2><div class="lista">' + c.dias.map((d) => {
        const dt = new Date(d.fecha + 'T12:00:00');
        return '<div class="card dia"><strong>' + NOMBRE_DIA[dt.getDay()] + ' ' + dt.getDate() + '</strong><span>' + Math.round(d.tempMin) + '° / ' + Math.round(d.tempMax) + '°</span><span>💧 ' + d.probLluvia + ' %</span></div>';
      }).join('') + '</div>' +
      (c.plan === 'GRATIS' ? '<div class="card"><p>Con el plan <strong>Plus</strong> ves el pronóstico a 7 días.</p><a class="btn sec full" href="#/planes">Ver planes</a></div>' : '') +
      '<p class="suave">Fuente: ' + esc(c.fuente) + ' · actualizado ' + esc(fechaHora(c.actualizadoEn)) + (c.desactualizado ? ' <span class="pastilla naranja">Desactualizado</span>' : '') + '</p>';
  } catch (e) { h = errorCard(e, true); }
  pantalla('Clima', h, { nav: 'clima' });
  const r = $('#reintentar'); if (r) r.onclick = render;
}

// ====================== alertas ======================
let filtroAlertas = 'TODAS';

async function vistaAlertas() {
  const todas = await Api.get('/api/alertas');
  const pintar = () => {
    const lista = todas.filter((a) => filtroAlertas === 'TODAS' || (filtroAlertas === 'OTRAS' ? (a.tipo === 'PRECIO' || a.tipo === 'LABOR') : a.tipo === filtroAlertas));
    $('#lista').innerHTML = lista.length ? lista.map((a) =>
      '<div class="card alerta-' + esc(a.nivel) + (a.leida ? '' : ' sinleer') + '" data-id="' + esc(a.id) + '"><div class="fila"><span style="font-size:1.8rem">' + iconoAlerta(a.tipo) + '</span><div class="grow">' +
      '<h3>' + esc(a.titulo) + '</h3>' + pastillaNivel(a.nivel) + '<p style="margin-top:6px">' + esc(a.mensaje) + '</p>' +
      (a.recomendacion ? '<p><strong>Qué hacer:</strong> ' + esc(a.recomendacion) + '</p>' : '') +
      '<p class="suave">' + esc(fechaHora(a.creadaEn)) + ' · Fuente: ' + esc(a.fuente || '—') + '</p></div></div>' +
      (a.leida ? '' : '<button class="chico sec" data-leer="' + esc(a.id) + '">Marcar como leída</button>') + '</div>').join('')
      : '<div class="card vacio">Sin alertas por ahora 🎉<br><span class="suave">Revisamos el clima de tu finca cada hora.</span></div>';
    $$('[data-leer]').forEach((b) => b.onclick = async () => {
      try { await Api.patch('/api/alertas/' + b.dataset.leer + '/leida'); const x = todas.find((a) => a.id === b.dataset.leer); if (x) x.leida = true; pintar(); }
      catch (e) { toast(e.offline ? 'Necesitas conexión' : e.message); }
    });
  };
  pantalla('Alertas',
    '<div class="tabs">' + [['TODAS', 'Todas'], ['CLIMA', 'Clima'], ['PLAGA', 'Plagas'], ['OTRAS', 'Otras']].map((t) =>
      '<button data-t="' + t[0] + '" class="' + (filtroAlertas === t[0] ? 'on' : '') + '">' + t[1] + '</button>').join('') + '</div>' +
    '<button class="sec full" id="evaluar">🔄 Revisar mi finca ahora</button><div id="lista" class="lista"></div>',
    { atras: '#/inicio', nav: 'inicio' });
  pintar();
  $$('.tabs button').forEach((b) => b.onclick = () => { filtroAlertas = b.dataset.t; $$('.tabs button').forEach((x) => x.classList.toggle('on', x === b)); pintar(); });
  $('#evaluar').onclick = async () => {
    try { const r = await Api.post('/api/alertas/evaluar'); toast(r.nuevas ? r.nuevas + ' alerta(s) nueva(s)' : 'Todo tranquilo: sin alertas nuevas'); render(); }
    catch (e) { toast(e.offline ? 'Necesitas conexión' : e.message); }
  };
}

// ====================== precios ======================
const precioEstado = { cat: 'PRODUCTO', plaza: 'Pasto', sel: null };

function grafico(puntos) {
  if (!puntos || puntos.length < 2) return '<p class="suave">Aún no hay suficientes datos para la tendencia.</p>';
  const W = 320, H = 150, P = 26;
  const ys = puntos.map((p) => p.precio);
  const min = Math.min.apply(null, ys), max = Math.max.apply(null, ys), rango = (max - min) || 1;
  const x = (i) => P + i * (W - 2 * P) / (puntos.length - 1);
  const y = (v) => H - P - ((v - min) / rango) * (H - 2 * P);
  const pts = puntos.map((p, i) => x(i).toFixed(1) + ',' + y(p.precio).toFixed(1)).join(' ');
  return '<svg class="grafico" viewBox="0 0 ' + W + ' ' + H + '" role="img" aria-label="Tendencia de precio de ' + puntos.length + ' días">' +
    '<polyline points="' + pts + '" fill="none" stroke="#2e7d32" stroke-width="3" stroke-linejoin="round"/>' +
    '<text x="' + P + '" y="14" font-size="11" fill="#5b6b5e">' + cop.format(max) + '</text>' +
    '<text x="' + P + '" y="' + (H - 6) + '" font-size="11" fill="#5b6b5e">' + cop.format(min) + '</text>' +
    '<text x="' + (W - P) + '" y="' + (H - 6) + '" font-size="11" fill="#5b6b5e" text-anchor="end">' + fecha(puntos[puntos.length - 1].fecha).slice(0, 5) + '</text></svg>';
}

async function vistaPrecios() {
  const [plazas, items] = await Promise.all([
    Api.get('/api/precios/plazas'),
    Api.get('/api/precios?categoria=' + precioEstado.cat + '&plaza=' + encodeURIComponent(precioEstado.plaza))
  ]);
  pantalla('Precios del mercado',
    '<div class="tabs"><button data-c="PRODUCTO" class="' + (precioEstado.cat === 'PRODUCTO' ? 'on' : '') + '">Productos</button>' +
    '<button data-c="INSUMO" class="' + (precioEstado.cat === 'INSUMO' ? 'on' : '') + '">Insumos</button></div>' +
    '<div class="chips">' + plazas.map((p) => '<button class="chip ' + (p === precioEstado.plaza ? 'on' : '') + '" data-p="' + esc(p) + '">' + esc(p) + '</button>').join('') + '</div>' +
    '<div class="lista">' + (items.length ? items.map((p) =>
      '<button class="card fila-link" style="text-align:left;font-weight:400;color:inherit;border-color:' + (precioEstado.sel === p.producto ? '#2e7d32' : '') + '" data-prod="' + esc(p.producto) + '">' +
      '<div class="fila"><div class="grow"><h3>' + esc(p.producto) + '</h3><p class="suave">' + esc(p.unidad) + ' · ' + esc(p.plaza) + '</p></div>' +
      '<div style="text-align:right"><strong>' + cop.format(p.precio) + '</strong><div>' + flecha(p.tendencia) + ' ' + (p.variacionPct > 0 ? '+' : '') + p.variacionPct + ' %</div></div></div></button>').join('')
      : '<div class="card vacio">Aún no hay precios cargados.</div>') + '</div>' +
    '<div id="detalle"></div>' +
    (items.length ? '<p class="suave">Fuente: ' + esc(items[0].fuente) + ' · ' + fecha(items[0].fecha) + (items[0].desactualizado ? ' <span class="pastilla naranja">Desactualizado</span>' : '') + '</p>' : ''),
    { nav: 'precios' });

  $$('.tabs button').forEach((b) => b.onclick = () => { precioEstado.cat = b.dataset.c; precioEstado.sel = null; render(); });
  $$('.chip').forEach((b) => b.onclick = () => { precioEstado.plaza = b.dataset.p; render(); });
  $$('[data-prod]').forEach((b) => b.onclick = () => { precioEstado.sel = b.dataset.prod; detallePrecio(); $$('[data-prod]').forEach((x) => x.style.borderColor = x === b ? '#2e7d32' : ''); });
  if (precioEstado.sel) detallePrecio();
}

async function detallePrecio() {
  const cont = $('#detalle');
  if (!cont) return;
  try {
    const t = await Api.get('/api/precios/tendencia?producto=' + encodeURIComponent(precioEstado.sel) + '&plaza=' + encodeURIComponent(precioEstado.plaza) + '&dias=30');
    cont.innerHTML = '<div class="card"><h3>Tendencia · ' + esc(t.producto) + '</h3><p class="suave">Últimos ' + t.puntos.length + ' días en ' + esc(t.plaza) + '</p>' + grafico(t.puntos) +
      (esPlus() ? '' : '<p class="suave" style="margin-top:8px">Con el plan Plus ves hasta 90 días.</p>') +
      '<form id="fo" style="margin-top:10px"><label for="obj">🎯 Avísame cuando llegue a (COP)</label><input id="obj" type="number" min="1" inputmode="numeric" placeholder="Ej. 2200000">' +
      '<div id="msg"></div><button class="full" style="margin-top:8px">Crear alerta de precio</button></form></div>';
    $('#fo').onsubmit = async (ev) => {
      ev.preventDefault();
      try { await Api.post('/api/precios/alertas', { producto: t.producto, plaza: t.plaza, precioObjetivo: Number($('#obj').value) }); toast('Alerta creada ✔'); $('#obj').value = ''; }
      catch (e) { mostrarError($('#msg'), e.offline ? new Error('Necesitas conexión') : e); }
    };
    cont.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  } catch (e) { cont.innerHTML = errorCard(e, false); }
}

// ====================== más ======================
async function vistaMas() {
  const p = await Api.get('/api/perfil');
  Api.setUser(p.usuario);
  const item = (href, ico, tit, sub) => '<a class="card fila-link" href="' + href + '"><div class="fila"><span style="font-size:1.6rem">' + ico + '</span><div class="grow"><h3 style="margin:0">' + tit + '</h3>' + (sub ? '<p class="suave">' + sub + '</p>' : '') + '</div><span aria-hidden="true">›</span></div></a>';
  pantalla('Más',
    '<div class="card"><div class="fila"><span style="font-size:2rem">👤</span><div class="grow"><h3 style="margin:0">' + esc(p.usuario.nombre) + '</h3><p class="suave">' + esc(p.usuario.celular) + '</p></div>' +
    '<span class="pastilla ' + (p.usuario.plan === 'PLUS' ? '' : 'gris') + '">Plan ' + (p.usuario.plan === 'PLUS' ? 'Plus' : 'Gratis') + '</span></div></div>' +
    '<div class="lista">' +
    item('#/alertas', '🔔', 'Alertas', 'Clima, plagas, precios y labores') +
    item('#/finca', '🏡', 'Mi finca', p.finca ? esc(p.finca.nombre) + ' · ' + esc(p.finca.municipio) : 'Aún no registrada') +
    item('#/notificaciones', '📲', 'Notificaciones', 'Push, WhatsApp, SMS y horario silencioso') +
    item('#/capacitaciones', '🎓', 'Capacitaciones', 'Videos y guías') +
    item('#/soporte', '👩‍🌾', 'Asesor técnico', 'Consulta a un experto') +
    item('#/planes', '⭐', 'Planes y suscripción', '') +
    item('#/datos', '🔒', 'Mis datos', 'Descargar o eliminar mi información') + '</div>' +
    '<button class="peligro full" id="salir">Cerrar sesión</button><p class="suave centro">AgroTec · API en <a href="/swagger-ui.html" target="_blank" rel="noopener">/swagger-ui.html</a></p>',
    { nav: 'mas' });
  $('#salir').onclick = () => { Api.logout(); };
}

async function vistaFinca() {
  const [p, municipios] = await Promise.all([Api.get('/api/perfil'), Api.get('/api/municipios')]);
  const f = p.finca || {};
  pantalla('Mi finca',
    '<form id="f" class="card" novalidate><label for="nom">Nombre de la finca</label><input id="nom" value="' + esc(f.nombre) + '" placeholder="Ej. Finca La Esperanza" required>' +
    '<label for="mun">Municipio</label><select id="mun">' + municipios.map((m) => '<option ' + (m.nombre === f.municipio ? 'selected' : '') + '>' + esc(m.nombre) + '</option>').join('') + '</select>' +
    '<label for="ver">Vereda / corregimiento</label><input id="ver" value="' + esc(f.vereda) + '" placeholder="Ej. Genoy">' +
    '<button type="button" class="sec full" id="gps" style="margin-top:12px">📍 Usar mi ubicación actual</button>' +
    '<p class="suave" id="coord">' + (f.latitud != null ? 'Ubicación guardada: ' + Number(f.latitud).toFixed(4) + ', ' + Number(f.longitud).toFixed(4) : 'Si no usas el GPS, tomamos la zona de tu municipio.') + '</p>' +
    '<div id="msg"></div><button class="full" style="margin-top:8px">Guardar mi finca</button></form>',
    { atras: '#/mas' });
  let lat = f.latitud != null ? f.latitud : null, lon = f.longitud != null ? f.longitud : null;
  $('#mun').onchange = () => { lat = null; lon = null; $('#coord').textContent = 'Usaremos la zona de tu municipio.'; };
  $('#gps').onclick = () => {
    if (!navigator.geolocation) { toast('Tu navegador no permite usar el GPS'); return; }
    navigator.geolocation.getCurrentPosition((pos) => {
      lat = pos.coords.latitude; lon = pos.coords.longitude;
      $('#coord').textContent = 'Ubicación detectada: ' + lat.toFixed(4) + ', ' + lon.toFixed(4);
    }, () => toast('No pudimos obtener tu ubicación'), { timeout: 10000 });
  };
  $('#f').onsubmit = async (ev) => {
    ev.preventDefault();
    try {
      await Api.put('/api/finca', { nombre: $('#nom').value, departamento: 'Nariño', municipio: $('#mun').value, vereda: $('#ver').value || null, latitud: lat, longitud: lon });
      toast('Finca guardada ✔'); location.hash = '#/inicio';
    } catch (e) { mostrarError($('#msg'), e.offline ? new Error('Necesitas conexión para guardar') : e); }
  };
}

// ====================== notificaciones ======================
async function vistaNotificaciones() {
  const [pr, perfil, bandeja] = await Promise.all([Api.get('/api/notificaciones/preferencias'), Api.get('/api/perfil'), Api.get('/api/notificaciones/bandeja')]);
  const plus = perfil.usuario.plan === 'PLUS';
  const chk = (id, on, label, dis) => '<label class="check"><input type="checkbox" id="' + id + '" ' + (on ? 'checked' : '') + (dis ? ' disabled' : '') + '> ' + label + '</label>';
  pantalla('Notificaciones',
    '<form id="f" class="card" novalidate><h3>¿Cómo quieres recibir tus alertas?</h3>' +
    chk('c-PUSH', pr.canales.includes('PUSH'), 'En la app (push)') + chk('c-WHATSAPP', pr.canales.includes('WHATSAPP'), 'WhatsApp' + (plus ? '' : ' <span class="pastilla gris">Plus</span>'), !plus) +
    chk('c-SMS', pr.canales.includes('SMS'), 'Mensaje de texto (SMS)' + (plus ? '' : ' <span class="pastilla gris">Plus</span>'), !plus) +
    '<label for="wa">Número para WhatsApp/SMS (opcional)</label><input id="wa" type="tel" inputmode="numeric" value="' + esc(pr.numeroWhatsapp) + '" placeholder="Si lo dejas vacío usamos tu celular">' +
    '<h3 style="margin-top:14px">Tipos de alerta</h3>' + chk('t-CLIMA', pr.tipos.includes('CLIMA'), 'Clima y heladas') + chk('t-PLAGA', pr.tipos.includes('PLAGA'), 'Plagas y enfermedades') +
    chk('t-PRECIO', pr.tipos.includes('PRECIO'), 'Cambios de precio') + chk('t-LABOR', pr.tipos.includes('LABOR'), 'Recordatorios de labores') +
    '<h3 style="margin-top:14px">Horario silencioso</h3><div class="dos"><div><label for="si">Desde</label><input id="si" type="time" value="' + esc(pr.silencioInicio) + '"></div><div><label for="sf">Hasta</label><input id="sf" type="time" value="' + esc(pr.silencioFin) + '"></div></div>' +
    '<div id="msg"></div><button class="full" style="margin-top:12px">Guardar preferencias</button></form>' +
    '<h2>Mensajes enviados (demostración)</h2><p class="suave">Los envíos por WhatsApp y SMS están simulados: aquí se ven los mensajes que se habrían enviado.</p>' +
    '<div class="lista">' + (bandeja.length ? bandeja.map((m) => '<div class="card"><p class="suave">' + esc(m.canal) + ' → ' + esc(m.destino) + ' · ' + esc(fechaHora(m.fecha)) + '</p><h3>' + esc(m.titulo) + '</h3><p>' + esc(m.mensaje) + '</p></div>').join('') : '<div class="vacio">Aún no se ha enviado ninguna notificación.</div>') + '</div>',
    { atras: '#/mas' });
  $('#f').onsubmit = async (ev) => {
    ev.preventDefault();
    const sel = (pref, lista) => lista.filter((x) => $('#' + pref + x).checked);
    try {
      await Api.put('/api/notificaciones/preferencias', {
        canales: sel('c-', ['PUSH', 'WHATSAPP', 'SMS']), tipos: sel('t-', ['CLIMA', 'PLAGA', 'PRECIO', 'LABOR']),
        numeroWhatsapp: $('#wa').value || null, silencioInicio: $('#si').value || null, silencioFin: $('#sf').value || null
      });
      toast('Preferencias guardadas ✔');
    } catch (e) { mostrarError($('#msg'), e.offline ? new Error('Necesitas conexión para guardar') : e); }
  };
}

// ====================== capacitaciones ======================
let filtroCap = '';

async function vistaCapacitaciones() {
  const lista = await Api.get('/api/capacitaciones' + (filtroCap ? '?cultivo=' + encodeURIComponent(filtroCap) : ''));
  pantalla('Capacitaciones',
    '<div class="chips">' + [['', 'Todas'], ['Café', 'Café'], ['Papa', 'Papa'], ['Maíz', 'Maíz']].map((c) => '<button class="chip ' + (filtroCap === c[0] ? 'on' : '') + '" data-c="' + esc(c[0]) + '">' + c[1] + '</button>').join('') + '</div>' +
    '<div class="lista">' + lista.map((c) => '<div class="card"><h3>' + (c.formato === 'VIDEO' ? '🎬 ' : '📄 ') + esc(c.titulo) + '</h3><p class="suave">' + esc(c.descripcion) + '</p>' +
      '<p class="suave">' + c.duracionMin + ' min · ' + esc(c.nivel) + (c.cultivo ? ' · ' + esc(c.cultivo) : '') + ' · ' + esc(c.entidad) + '</p>' +
      '<div class="barra" aria-label="Avance ' + c.avance + '%"><i style="width:' + c.avance + '%"></i></div><p class="suave">' + c.avance + ' % completado</p>' +
      (c.avance < 100 ? '<button class="chico sec" data-av="' + esc(c.id) + '" data-pct="' + Math.min(100, c.avance + 25) + '">Registrar avance (+25 %)</button>' : '<span class="pastilla">✔ Completado</span>') + '</div>').join('') + '</div>',
    { atras: '#/mas' });
  $$('.chip').forEach((b) => b.onclick = () => { filtroCap = b.dataset.c; render(); });
  $$('[data-av]').forEach((b) => b.onclick = async () => {
    try { await Api.put('/api/capacitaciones/' + b.dataset.av + '/avance', { porcentaje: Number(b.dataset.pct) }); render(); }
    catch (e) { toast(e.offline ? 'Necesitas conexión' : e.message); }
  });
}

// ====================== soporte técnico ======================
async function vistaSoporte() {
  const [perfil, consultas] = await Promise.all([Api.get('/api/perfil'), Api.get('/api/soporte/consultas')]);
  Api.setUser(perfil.usuario);
  const plus = perfil.usuario.plan === 'PLUS';
  const estado = { ENVIADA: 'Enviada', EN_REVISION: 'En revisión', RESPONDIDA: 'Respondida' };
  pantalla('Asesor técnico',
    (plus ? '<form id="f" class="card" novalidate><label for="msj">Cuéntale a tu asesor qué está pasando</label><textarea id="msj" rows="4" maxlength="1000" placeholder="Ej. Mis hojas de café tienen manchas amarillas…"></textarea>' +
      '<label for="foto">Enlace de una foto (opcional)</label><input id="foto" type="url" placeholder="https://…"><div id="msg"></div><button class="full" style="margin-top:10px">Enviar consulta</button></form>'
      : '<div class="card"><h3>👩‍🌾 Función del plan Plus</h3><p>Con Plus puedes enviar consultas con fotos a un asesor y recibir respuesta en la app o por WhatsApp.</p><a class="btn full" href="#/planes">Ver planes</a></div>') +
    '<h2>Mis consultas</h2><div class="lista">' + (consultas.length ? consultas.map((c) => '<div class="card"><div class="fila"><div class="grow"><p>' + esc(c.mensaje) + '</p><p class="suave">' + esc(fechaHora(c.creadaEn)) + '</p></div><span class="pastilla ' + (c.estado === 'RESPONDIDA' ? '' : 'gris') + '">' + estado[c.estado] + '</span></div>' +
      (c.respuesta ? '<div class="ok" style="margin-top:8px"><strong>' + esc(c.asesorNombre || 'Asesor') + ':</strong> ' + esc(c.respuesta) + '</div>' : '') + '</div>').join('') : '<div class="vacio">Aún no has enviado consultas.</div>') + '</div>',
    { atras: '#/mas' });
  const f = $('#f');
  if (f) f.onsubmit = async (ev) => {
    ev.preventDefault();
    try { await Api.post('/api/soporte/consultas', { mensaje: $('#msj').value, fotos: $('#foto').value ? [$('#foto').value] : [] }); toast('Consulta enviada ✔'); render(); }
    catch (e) { mostrarError($('#msg'), e.offline ? new Error('Necesitas conexión para enviar') : e); }
  };
}

// ====================== planes ======================
async function vistaPlanes() {
  const [planes, estado, perfil] = await Promise.all([Api.get('/api/planes'), Api.get('/api/suscripcion'), Api.get('/api/perfil')]);
  Api.setUser(perfil.usuario);
  const tarjeta = (p) => '<div class="card" style="' + (p.plan === 'PLUS' ? 'border:2px solid #2e7d32' : '') + '"><div class="fila"><h3 class="grow" style="margin:0">' + esc(p.nombre) + '</h3><strong>' + (p.precioMensual ? cop.format(p.precioMensual) + ' / mes' : '$0') + '</strong></div>' +
    '<ul style="padding-left:20px;margin:10px 0">' + p.beneficios.map((b) => '<li>' + esc(b) + '</li>').join('') + '</ul>' +
    (p.plan === 'GRATIS' ? (estado.plan === 'GRATIS' ? '<span class="pastilla">Tu plan actual</span>' : '') : '') + '</div>';
  pantalla('Planes',
    planes.map(tarjeta).join('') +
    (estado.plan === 'PLUS'
      ? '<div class="ok">⭐ Tienes Plus hasta el <strong>' + fecha(estado.vigenteHasta) + '</strong>' + (estado.cancelada ? ' (no se renovará)' : '') + '</div>' +
        (estado.cancelada ? '' : '<button class="peligro full" id="cancelar">Cancelar suscripción</button>')
      : '') +
    '<form id="f" class="card" novalidate><h3>' + (estado.plan === 'PLUS' ? 'Extender 30 días más' : 'Suscribirme al plan Plus') + '</h3>' +
    '<label for="met">Método de pago</label><select id="met"><option value="NEQUI">Nequi</option><option value="DAVIPLATA">Daviplata</option><option value="TARJETA">Tarjeta</option><option value="PSE">PSE</option></select>' +
    '<label for="tok">Token de la pasarela (demo)</label><input id="tok" placeholder="Déjalo vacío para aprobar · escribe RECHAZAR para probar un rechazo">' +
    '<p class="suave">Pago simulado: no se cobra dinero real.</p><div id="msg"></div><button class="full" style="margin-top:8px">Pagar ' + cop.format(30000) + '</button></form>',
    { atras: '#/mas' });
  $('#f').onsubmit = async (ev) => {
    ev.preventDefault();
    try { const r = await Api.post('/api/suscripcion', { plan: 'PLUS', metodoPago: $('#met').value, tokenPago: $('#tok').value || null }); toast(r.mensaje || 'Suscripción activa ✔'); render(); }
    catch (e) { mostrarError($('#msg'), e.offline ? new Error('Necesitas conexión para pagar') : e); }
  };
  const c = $('#cancelar');
  if (c) c.onclick = async () => {
    if (!confirm('¿Cancelar la suscripción? Conservas Plus hasta el final del período pagado.')) return;
    try { const r = await Api.del('/api/suscripcion'); toast(r.mensaje); render(); } catch (e) { toast(e.offline ? 'Necesitas conexión' : e.message); }
  };
}

// ====================== mis datos ======================
async function vistaDatos() {
  pantalla('Mis datos',
    '<div class="card"><h3>Tus derechos (Ley 1581 de 2012)</h3><p>Puedes consultar, descargar y eliminar toda la información que AgroTec guarda sobre ti.</p>' +
    '<button class="sec full" id="exportar">⬇️ Descargar mis datos</button></div>' +
    '<div class="card"><h3>Eliminar mi cuenta</h3><p class="suave">Se borrarán tu perfil, finca, cultivos, actividades, alertas y consultas. No se puede deshacer.</p><button class="peligro full" id="eliminar">Eliminar mi cuenta y mis datos</button></div>',
    { atras: '#/mas' });
  $('#exportar').onclick = async () => {
    try {
      const d = await Api.get('/api/me/datos');
      const blob = new Blob([JSON.stringify(d, null, 2)], { type: 'application/json' });
      const a = document.createElement('a'); a.href = URL.createObjectURL(blob); a.download = 'mis-datos-agrotec.json'; a.click();
      setTimeout(() => URL.revokeObjectURL(a.href), 1000); toast('Descarga lista');
    } catch (e) { toast(e.offline ? 'Necesitas conexión' : e.message); }
  };
  $('#eliminar').onclick = async () => {
    if (!confirm('¿Seguro? Se eliminarán tu cuenta y TODOS tus datos de forma permanente.')) return;
    try { await Api.del('/api/me'); toast('Tu cuenta fue eliminada'); Api.logout(); } catch (e) { toast(e.offline ? 'Necesitas conexión' : e.message); }
  };
}

// ====================== arranque ======================
if ('serviceWorker' in navigator) {
  navigator.serviceWorker.register('sw.js').catch(() => { /* sin soporte o contexto no seguro: la app funciona igual */ });
}
if (Api.token) sincronizarAhora(false);
render();
