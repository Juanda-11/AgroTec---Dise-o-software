/* Service worker de AgroTec: guarda la "carcasa" de la app para que abra sin conexión (RF-10).
   Los datos de la API se guardan aparte, en localStorage (ver js/api.js). */
const VERSION = 'agrotec-v1';
const CARCASA = ['./', 'index.html', 'css/styles.css', 'js/api.js', 'js/app.js', 'manifest.webmanifest', 'icon.svg'];

self.addEventListener('install', (e) => {
  e.waitUntil(caches.open(VERSION).then((c) => c.addAll(CARCASA)).then(() => self.skipWaiting()));
});

self.addEventListener('activate', (e) => {
  e.waitUntil(
    caches.keys()
      .then((claves) => Promise.all(claves.filter((k) => k !== VERSION).map((k) => caches.delete(k))))
      .then(() => self.clients.claim())
  );
});

self.addEventListener('fetch', (e) => {
  const req = e.request;
  const url = new URL(req.url);
  // La API nunca se cachea aquí; solo los archivos estáticos del mismo origen.
  if (req.method !== 'GET' || url.origin !== location.origin || url.pathname.startsWith('/api/')) return;
  e.respondWith(
    fetch(req)
      .then((res) => {
        const copia = res.clone();
        caches.open(VERSION).then((c) => c.put(req, copia));
        return res;
      })
      .catch(() => caches.match(req).then((r) => r || caches.match('index.html')))
  );
});
