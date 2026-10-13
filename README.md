# Gestor de Reservas de Hotel

Aplicación web para gestionar **usuarios, clientes, habitaciones y reservas** de un hotel. Es la migración de un
monolito Java/Swing (MVC) a una arquitectura con **dos backends Spring Boot** —un backend de gestión y un
microservicio de habitaciones— y un **frontend React**. Los datos se guardan en memoria (sin base de datos).

## Arquitectura

```
Frontend React (:5173)
   ├──► backend-gestion (:8080)  ·  usuarios · clientes · reservas
   │          │
   │          │  HTTP/JSON
   │          ▼
   └──► microservicio-habitaciones (:8081)  ·  habitaciones
```

- **backend-gestion** (puerto 8080): usuarios, clientes y reservas.
- **microservicio-habitaciones** (puerto 8081): habitaciones, cálculo de precios y estado.
- **frontend** (puerto 5173): interfaz web que consume ambos backends.

Cuando se crea una reserva, o se hace check-in o check-out, el backend de gestión consulta y actualiza el
microservicio por HTTP: valida las habitaciones, calcula el precio por noche y cambia su estado
(check-in → ocupada, check-out → limpieza).

## Requisitos

- JDK 26
- Node.js 22.12 o superior
- Git

## Cómo ejecutarlo

Cada servicio en su propia terminal y en este orden:

```bash
# 1. Microservicio de habitaciones  ->  http://localhost:8081
cd microservicio-habitaciones
./mvnw spring-boot:run              # Windows: mvnw.cmd spring-boot:run

# 2. Backend de gestión             ->  http://localhost:8080
cd backend-gestion
./mvnw spring-boot:run

# 3. Frontend                       ->  http://localhost:5173
cd frontend
npm install                         # Solo la primera vez
npm run dev
```

Los datos de demostración se cargan al iniciar cada servicio y se pierden al reiniciarlo.

## Estructura del repositorio

```
.
├── backend-gestion/              Spring Boot (usuarios, clientes, reservas)
├── microservicio-habitaciones/   Spring Boot (habitaciones)
└── frontend/                     React + Vite + TypeScript
```

## Equipo

| Integrante | Código de estudiante |
|---|---|
| Yaiza Angelina Sanchez Dueñez | 2220232034 |
| Nicolas Gonzalez Ortiz | 2220261089 |
