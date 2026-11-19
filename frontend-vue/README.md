# Frontend Vue — Gestor de Reservas de Hotel

Segunda interfaz web del gestor de reservas. Es **igual a la interfaz React** (mismas pantallas, rutas, columnas,
formularios, validaciones y mensajes), pero hecha con **Vue 3 y Vuetify**. Consume los mismos dos backends, así que las dos
interfaces trabajan sobre los mismos datos de MongoDB: lo que se crea en una aparece en la otra.

## Tecnologías

Vue 3.5 (`<script setup lang="ts">`) · Vite 8 · TypeScript 6 · Vuetify 4 · Vue Router 5 · Axios 1 · `@mdi/font` (íconos) ·
ESLint 10 y Oxlint

## Requisitos

- Node.js 22.18 o superior (`node -v`; el `package.json` exige `^22.18.0` o `>=24.12.0`).
- MongoDB encendido y los **dos backends** arrancados: el microservicio de habitaciones (`:8081`) y el backend de
  gestión (`:8080`). Cómo arrancarlos está en el `README.md` de la raíz del repositorio.

## Cómo ejecutarlo

Desde la carpeta `frontend-vue/`:

```powershell
npm install                          # solo la primera vez
Copy-Item .env.example .env          # solo la primera vez (macOS/Linux: cp .env.example .env)
npm run dev                          # http://localhost:5174
```

El puerto **5174 es fijo** (`strictPort` en `vite.config.ts`): los backends solo aceptan peticiones de `5173` (React) y
`5174` (Vue). Si el puerto está ocupado, Vite se detiene con un error en lugar de cambiar de puerto.

## Variables de entorno

El archivo `.env` no se sube al repositorio; se crea copiando `.env.example`.

| Variable | Valor | Para qué sirve |
|---|---|---|
| `VITE_GESTION_API_URL` | `http://localhost:8080/api` | Backend de gestión: clientes, usuarios y reservas |
| `VITE_HABITACIONES_API_URL` | `http://localhost:8081/api` | Microservicio de habitaciones |

Si se cambia el `.env`, hay que detener y volver a ejecutar `npm run dev`.

## Comandos

| Comando | Qué hace |
|---|---|
| `npm run dev` | Arranca la interfaz en modo desarrollo en `http://localhost:5174` |
| `npm run build` | Revisa los tipos (`vue-tsc`) y genera la versión final en `dist/` |
| `npm run lint` | Revisa el código con Oxlint y ESLint (**corrige solo** lo que puede, así que modifica archivos) |
| `npm run preview` | Sirve localmente la versión generada por `npm run build` |

## Pantallas

| Ruta | Pantalla | Backend que usa |
|---|---|---|
| `/reservas` | Lista con filtros por estado y por cliente; crear, editar, ver detalle, confirmar, check-in, check-out, cancelar y eliminar | Gestión (`:8080`) |
| `/habitaciones` | Lista con filtros por estado y por tipo; crear, editar, eliminar y cambiar el estado desde el chip | Habitaciones (`:8081`) |
| `/clientes` | Lista; crear, editar y eliminar. El país de residencia se elige de una lista y rellena la nacionalidad | Gestión (`:8080`) |
| `/usuarios` | Lista; crear, editar y eliminar | Gestión (`:8080`) |

La raíz `/` y cualquier ruta desconocida llevan a `/reservas`.

**Filtros.** Los aplica el servidor con los parámetros de la API: `GET /api/habitaciones?estado=&tipoHabitacion=` y
`GET /api/reservas?estado=&clienteId=`. Se puede usar uno solo o los dos a la vez; un filtro en «Todos» no se envía.

**Países.** Al escribir en «País de residencia» se sugieren los países que empiezan por esas letras (sin importar tildes
ni mayúsculas) y, al elegir uno, la nacionalidad se rellena sola (Colombia → Colombiana). Los dos campos siguen siendo
editables. La lista es fija y está en `src/utils/paises.ts`.

## Estructura

```
src/
├── api/          COPIA EXACTA de frontend/src/api: llamadas HTTP (Axios) y traducción de errores
├── types/        COPIA EXACTA de frontend/src/types: tipos de cada entidad
├── utils/        COPIA EXACTA de frontend/src/utils: constantes, formatos, validaciones y lista de países
├── components/   piezas reutilizables (.vue): encabezado, diálogo de confirmación, chip de estado, alertas…
├── composables/  useLista (carga de tablas) y useNotify (mensajes de éxito y error)
├── layouts/      MainLayout.vue: barra superior con el menú de las 4 secciones
├── views/        una carpeta por pantalla, con su vista y sus diálogos
├── plugins/      vuetify.ts: tema con los mismos colores que React · vuetify.css: tipografía, botones y tablas
├── router/       index.ts: tabla de rutas
└── main.ts · App.vue
```

`api/`, `types/` y `utils/` se copiaron sin cambios de la interfaz React para que las dos validen y muestren los datos de
la misma forma. Si se cambia algo en una de esas carpetas, hay que cambiarlo también en el otro frontend.

## Equivalencias con la interfaz React

| React | Vue |
|---|---|
| `pages/` | `views/` |
| `hooks/` | `composables/` |
| `theme.ts` (MUI) | `plugins/vuetify.ts` y `plugins/vuetify.css` (Vuetify) |
| `routes.tsx` | `router/index.ts` |

## Si algo falla

| Síntoma | Qué revisar |
|---|---|
| «Port 5174 is already in use» | Hay otra ventana de Vue abierta, o la interfaz React tomó el 5174 porque el 5173 estaba ocupado. Cerrar ese proceso |
| La pantalla muestra «Sin conexión» o el navegador habla de CORS | El backend de esa pantalla está apagado, o falta el archivo `.env` |
| Al crear o pasar una reserva a check-in sale el mensaje de servicio no disponible | El microservicio de habitaciones (`:8081`) está apagado; es el comportamiento esperado (503) |
| `npm install` o `npm run dev` fallan por la versión de Node | Instalar Node 22.18 o superior |
