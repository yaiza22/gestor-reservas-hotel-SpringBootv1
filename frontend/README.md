# Frontend React — Gestor de Reservas de Hotel

Primera interfaz web del gestor de reservas. Permite administrar **reservas, habitaciones, clientes y usuarios** desde el
navegador y consume los dos backends del proyecto. Desde la versión 2.0 los datos se guardan en **MongoDB**, así que todo
lo que se crea aquí también aparece en la segunda interfaz (Vue, puerto 5174), porque las dos usan los mismos servicios.

## Tecnologías

React 19 · Vite 8 · TypeScript 6 · MUI 9 · React Router 8 (paquete `react-router`) · Axios 1 · ESLint 10

## Requisitos

- Node.js 22.18 o superior (`node -v`).
- MongoDB encendido y los **dos backends** arrancados: el microservicio de habitaciones (`:8081`) y el backend de
  gestión (`:8080`). Cómo arrancarlos está en el `README.md` de la raíz del repositorio.

## Cómo ejecutarlo

Desde la carpeta `frontend/`:

```powershell
npm install                          # solo la primera vez
Copy-Item .env.example .env          # solo la primera vez (macOS/Linux: cp .env.example .env)
npm run dev                          # http://localhost:5173
```

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
| `npm run dev` | Arranca la interfaz en modo desarrollo en `http://localhost:5173` |
| `npm run build` | Revisa los tipos de TypeScript y genera la versión final en `dist/` |
| `npm run lint` | Revisa el estilo del código con ESLint |
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
├── api/         llamadas HTTP a los backends (Axios) y traducción de errores
├── components/  piezas reutilizables: encabezado, diálogo de confirmación, chip de estado, alertas…
├── hooks/       useLista (carga de tablas) y useNotify (mensajes de éxito y error)
├── layouts/     MainLayout: barra superior con el menú de las 4 secciones
├── pages/       una carpeta por pantalla, con su página y sus diálogos
├── types/       tipos de TypeScript de cada entidad
├── utils/       constantes, formatos (pesos y dd/MM/yyyy), validaciones de formularios y lista de países
├── theme.ts     colores y estilos globales de MUI
├── routes.tsx   tabla de rutas
└── main.tsx · App.tsx
```

`src/api/`, `src/types/` y `src/utils/` tienen una copia exacta en el frontend Vue: si se cambia algo en una, hay que
cambiarlo también en la otra.

## Si algo falla

| Síntoma | Qué revisar |
|---|---|
| La pantalla muestra «Sin conexión» | El backend de esa pantalla está apagado, o falta el archivo `.env` |
| Al crear o pasar una reserva a check-in sale el mensaje de servicio no disponible | El microservicio de habitaciones (`:8081`) está apagado; es el comportamiento esperado (503) |
| Vite arranca en otro puerto (por ejemplo 5174) | El puerto 5173 estaba ocupado. Hay que cerrar el otro proceso: los backends solo aceptan 5173 y 5174 (CORS), y el 5174 es el de la interfaz Vue |
| `npm run dev` falla por la versión de Node | Instalar Node 22.18 o superior |
