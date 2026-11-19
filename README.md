# Gestor de Reservas de Hotel

Aplicación web para gestionar **usuarios, clientes, habitaciones y reservas** de un hotel. Es la migración de un
monolito Java/Swing (MVC) a una arquitectura con **dos backends Spring Boot** —un backend de gestión y un
microservicio de habitaciones—, **dos interfaces web** (React y Vue) y datos persistentes en **MongoDB**. Cada backend
documenta su API con **Swagger (OpenAPI)**.

## Arquitectura

```
Frontend React (:5173) ─┐
Frontend Vue   (:5174) ─┤
                        ├──► backend-gestion (:8080)  ·  usuarios · clientes · reservas ───► MongoDB hotel_gestion
                        │          │  HTTP/JSON
                        │          ▼
                        └──► microservicio-habitaciones (:8081)  ·  habitaciones ──────────► MongoDB hotel_habitaciones
```

- **backend-gestion** (puerto 8080): usuarios, clientes y reservas. Base de datos `hotel_gestion`.
- **microservicio-habitaciones** (puerto 8081): habitaciones, cálculo de precios y estado. Base de datos `hotel_habitaciones`.
- **frontend** (puerto 5173): interfaz web en React.
- **frontend-vue** (puerto 5174): la misma interfaz, hecha con Vue.

Cuando se crea una reserva, o se hace check-in o check-out, el backend de gestión consulta y actualiza el
microservicio por HTTP: valida las habitaciones, calcula el precio por noche y cambia su estado
(check-in → ocupada, check-out → limpieza). Cada servicio es dueño de su propia base de datos: gestión nunca lee la
base de habitaciones.

## Tecnologías

| Parte | Tecnologías |
|---|---|
| Backends | Java 25, Spring Boot 4.1.1 (Maven), Spring Data MongoDB, Bean Validation, Lombok, springdoc-openapi 3.1.1 |
| Base de datos | MongoDB Community Server 8.x (local) |
| Frontend React | React 19, Vite 8, TypeScript, MUI 9, React Router, Axios |
| Frontend Vue | Vue 3.5, Vite 8, TypeScript, Vuetify 4, Vue Router, Axios |

## Requisitos

- JDK 25
- Node.js 22.18 o superior
- Git
- MongoDB Community Server 8.x, MongoDB Shell (`mongosh`) y MongoDB Compass
- Postman (opcional, para probar la API)

Maven no hace falta instalarlo: cada backend trae su propio `mvnw`.

## Cómo ejecutarlo

**1. Comprobar que MongoDB está encendido** (en Windows se instala como servicio):

```powershell
Get-Service MongoDB                                # Status: Running
mongosh --eval "db.runCommand({ ping: 1 })"        # debe responder { ok: 1 }
```

**2. Crear el archivo `.env` de cada frontend** (solo la primera vez):

```powershell
Copy-Item frontend/.env.example frontend/.env
Copy-Item frontend-vue/.env.example frontend-vue/.env
```

**3. Arrancar cada servicio en su propia terminal y en este orden:**

```bash
# 1. Microservicio de habitaciones  ->  http://localhost:8081
cd microservicio-habitaciones
./mvnw spring-boot:run              # Windows: mvnw.cmd spring-boot:run

# 2. Backend de gestión             ->  http://localhost:8080
cd backend-gestion
./mvnw spring-boot:run              # Windows: mvnw.cmd spring-boot:run

# 3. Frontend React                 ->  http://localhost:5173
cd frontend
npm install                         # solo la primera vez
npm run dev

# 4. Frontend Vue                   ->  http://localhost:5174
cd frontend-vue
npm install                         # solo la primera vez
npm run dev
```

## Direcciones

| Qué | Dirección |
|---|---|
| Interfaz React | http://localhost:5173 |
| Interfaz Vue | http://localhost:5174 |
| Swagger de gestión | http://localhost:8080/swagger-ui.html |
| Swagger de habitaciones | http://localhost:8081/swagger-ui.html |
| OpenAPI (JSON) | http://localhost:8080/v3/api-docs · http://localhost:8081/v3/api-docs |
| MongoDB (Compass) | `mongodb://localhost:27017` → bases `hotel_gestion` y `hotel_habitaciones` |

La tabla de endpoints y métodos HTTP está en `documentacion/API REST_endpoints_metodos_HTTP.pdf`.

## Datos

Al primer arranque se cargan datos de demostración (10 habitaciones, 4 clientes y 2 usuarios). En los siguientes
arranques **no se repiten**, y todo lo que se crea, edita o elimina se conserva en MongoDB.

Para dejar las bases como el primer día, detener los dos backends y ejecutar desde la raíz del repositorio:

```bash
mongosh --file scripts/reiniciar-bd.js
```

Al volver a arrancar los servicios (microservicio primero) se cargan de nuevo los datos de demostración.

## Estructura del repositorio

```
.
├── backend-gestion/              Spring Boot (usuarios, clientes, reservas)  ·  puerto 8080
├── microservicio-habitaciones/   Spring Boot (habitaciones)                  ·  puerto 8081
├── frontend/                     React + Vite + TypeScript                   ·  puerto 5173
├── frontend-vue/                 Vue + Vuetify + TypeScript                  ·  puerto 5174
├── scripts/                      reiniciar-bd.js (reinicia las bases de MongoDB)
└── documentacion/                Documento de la API REST (endpoints y métodos HTTP)
```

## Versiones

| Etiqueta | Contenido |
|---|---|
| `v1.0.0` | CRUD con datos en memoria, 2 backends y frontend React |
| `v2.0.0` | Datos persistentes en MongoDB, documentación Swagger y segunda interfaz en Vue |

## Equipo

| Integrante | Código de estudiante |
|---|---|
| Yaiza Angelina Sanchez Dueñez | 2220232034 |
| Nicolas Gonzalez Ortiz | 2220261089 |