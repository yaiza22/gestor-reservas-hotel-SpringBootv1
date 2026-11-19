# Frontend Vue — Gestión de Habitaciones de Hotel

Interfaz del **microservicio de habitaciones**, hecha con **Vue 3 y Vuetify**. Tiene una sola pantalla, Habitaciones, y
solo habla con el microservicio (`:8081`). Reservas, clientes y usuarios se administran en la otra interfaz, el **Gestor
de Reservas de Hotel** (React, puerto 5173). Las dos trabajan sobre los mismos datos de MongoDB: una habitación creada
aquí se puede elegir al crear una reserva en React, y el check-in de React cambia su estado aquí.

## Tecnologías

Vue 3.5 (`<script setup lang="ts">`) · Vite 8 · TypeScript 6 · Vuetify 4 · Vue Router 5 · Axios 1 · `@mdi/font` (íconos) ·
ESLint 10 y Oxlint

## Requisitos

- Node.js 22.18 o superior (`node -v`; el `package.json` exige `^22.18.0` o `>=24.12.0`).
- MongoDB encendido y el **microservicio de habitaciones** (`:8081`) arrancado. Cómo arrancarlo está en el `README.md`
  de la raíz del repositorio.

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
| `VITE_HABITACIONES_API_URL` | `http://localhost:8081/api` | Microservicio de habitaciones (el que usa esta interfaz) |
| `VITE_GESTION_API_URL` | `http://localhost:8080/api` | Backend de gestión. Esta interfaz no lo usa, pero `src/api/` es copia exacta de React y lo declara |

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
| `/habitaciones` | Lista con filtros por estado y por tipo; crear, editar, eliminar y cambiar el estado desde el chip | Habitaciones (`:8081`) |

La raíz `/` y cualquier ruta desconocida llevan a `/habitaciones`.

**Filtros.** Los aplica el servidor con los parámetros de la API `GET /api/habitaciones?estado=&tipoHabitacion=`. Se
puede usar uno solo o los dos a la vez; un filtro en «Todos» no se envía.

## Estructura

```
src/
├── api/          COPIA EXACTA de frontend/src/api: llamadas HTTP (Axios) y traducción de errores
├── types/        COPIA EXACTA de frontend/src/types: tipos de cada entidad
├── utils/        COPIA EXACTA de frontend/src/utils: constantes, formatos, validaciones y lista de países
├── components/   piezas reutilizables (.vue): encabezado, diálogo de confirmación, chip de estado, alertas…
├── composables/  useLista (carga de tablas) y useNotify (mensajes de éxito y error)
├── layouts/      MainLayout.vue: barra superior «Gestión de Habitaciones de Hotel»
├── views/        habitaciones/ (la pantalla de esta interfaz); clientes/, usuarios/ y reservas/ ya no tienen ruta
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
| La pantalla muestra «No se pudo comunicar con el servicio de habitaciones» | El microservicio (`:8081`) está apagado, o falta el archivo `.env` |
| `npm install` o `npm run dev` fallan por la versión de Node | Instalar Node 22.18 o superior |
