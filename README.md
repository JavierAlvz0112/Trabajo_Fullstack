# FoodTruck Microservicios — DSY1103_010D

Sistema de gestión para un FoodTruck desarrollado con arquitectura de microservicios usando Spring Boot, Docker y MySQL.

---

## Integrantes

| Nombre | GitHub |
|--------|--------|
| Javier Álvarez | [@JavierAlvz0112](https://github.com/JavierAlvz0112) |
| Carlos Salazar | [@Carlinos06](https://github.com/Carlinos06) |

---

## Microservicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| `api-gateway` | 9090 | Punto de entrada único — enruta y valida JWT |
| `auth-service` | 9096 | Registro e inicio de sesión — genera tokens JWT |
| `usuarios-service` | 9094 | Gestión de usuarios del sistema |
| `pagos-service` | 9093 | Registro y consulta de pagos por pedido |
| `pedidos-service` | 9092 | Creación y seguimiento de pedidos |
| `productos-service` | 9095 | Catálogo de comidas y bebidas del FoodTruck |
| `almacen-service` | 9091 | Control de stock e inventario de productos |
| `envios-service` | 9097 | Gestión de envíos y seguimiento de estado |

---

## Rutas del API Gateway

Todos los requests van por `http://localhost:9090`. El gateway valida el JWT en cada request excepto `/auth/**`.

| Ruta | Servicio destino | Auth |
|------|-----------------|------|
| `POST /auth/register` | auth-service | ❌ libre |
| `POST /auth/login` | auth-service | ❌ libre |
| `GET/POST /api/usuarios/**` | usuarios-service | ✅ JWT |
| `GET/POST /api/pagos/**` | pagos-service | ✅ JWT |
| `GET/POST /api/pedidos/**` | pedidos-service | ✅ JWT |
| `GET/POST /api/productos/**` | productos-service | ✅ JWT |
| `GET/POST /api/almacen/**` | almacen-service | ✅ JWT |
| `GET/POST /api/envios/**` | envios-service | ✅ JWT |

---

## Documentación Swagger

Cada servicio tiene Swagger UI disponible directamente (sin JWT):

| Servicio | URL Swagger |
|----------|-------------|
| auth-service | http://localhost:9096/doc/swagger-ui.html |
| usuarios-service | http://localhost:9094/doc/swagger-ui.html |
| pagos-service | http://localhost:9093/doc/swagger-ui.html |
| pedidos-service | http://localhost:9092/doc/swagger-ui.html |
| productos-service | http://localhost:9095/doc/swagger-ui.html |
| almacen-service | http://localhost:9091/doc/swagger-ui.html |
| envios-service | http://localhost:9097/doc/swagger-ui.html |

---

## Tecnologías

- **Java 21** + **Spring Boot 4.0.6**
- **Spring Cloud Gateway 2025.1.1** (api-gateway usa Spring Boot 3.3.5)
- **Spring Data JPA** + **MySQL 8**
- **JWT** (JJWT 0.12.5) con HMAC-SHA384
- **Swagger/OpenAPI** (springdoc 3.0.3)
- **Docker** + **Docker Compose**
- **Lombok** para reducir boilerplate
- **JUnit 5** + **Mockito** para pruebas unitarias

---

## Instrucciones de ejecución

### Requisitos previos

- Java 21 instalado
- Maven instalado
- MySQL corriendo en `localhost:3306` (sin password)
- Docker Desktop instalado y corriendo

---

### Opción A — Ejecución con Docker (recomendado)

**1. Compilar todos los servicios:**

```bash
# Desde la raíz del proyecto, compilar cada servicio:
cd api-gateway && mvn clean package -DskipTests && cd ..
cd auth-service && mvn clean package -DskipTests && cd ..
cd usuarios-service/usuarios-service && mvn clean package -DskipTests && cd ../..
cd pagos-service && mvn clean package -DskipTests && cd ..
cd pedidos-service && mvn clean package -DskipTests && cd ..
cd productos-service && mvn clean package -DskipTests && cd ..
cd almacen-service && mvn clean package -DskipTests && cd ..
cd envios-service && mvn clean package -DskipTests && cd ..
```

**2. Levantar con Docker Compose:**

```bash
docker-compose up --build
```

**3. Verificar que todos los servicios estén corriendo:**

```bash
docker ps
```

---

### Opción B — Ejecución local (sin Docker)

Levantar cada servicio individualmente desde IntelliJ o con:

```bash
cd pagos-service
mvn spring-boot:run
```

Repetir para cada servicio. Las bases de datos se crean automáticamente con `createDatabaseIfNotExist=true`.

---

## Autenticación JWT

**1. Registrar usuario:**
```bash
POST http://localhost:9090/auth/register
Content-Type: application/json

{
  "email": "test@foodtruck.com",
  "password": "pass123"
}
```

**2. Hacer login y obtener token:**
```bash
POST http://localhost:9090/auth/login
Content-Type: application/json

{
  "email": "test@foodtruck.com",
  "password": "pass123"
}
```

Respuesta:
```json
{
  "status": "ok",
  "token": "eyJhbGciOiJIUzM4NiJ9..."
}
```

**3. Usar el token en requests:**
```
Authorization: Bearer eyJhbGciOiJIUzM4NiJ9...
```

---

## Bases de datos

Cada microservicio tiene su propia base de datos MySQL independiente:

| Base de datos | Servicio |
|---------------|----------|
| `bd_auth` | auth-service |
| `bd_usuarios` | usuarios-service |
| `bd_pagos` | pagos-service |
| `bd_pedidos` | pedidos-service |
| `bd_productos` | productos-service |
| `bd_almacen` | almacen-service |
| `bd_envios` | envios-service |

Las tablas se crean automáticamente al arrancar cada servicio con `spring.jpa.hibernate.ddl-auto=create`.

---

## Pruebas unitarias

Los tests se ubican en `src/test/java/` de cada servicio y siguen el patrón **Given-When-Then** con JUnit 5 y Mockito.

```bash
# Correr tests de un servicio
cd pagos-service
mvn test
```

Servicios con pruebas implementadas: `pagos-service`, `usuarios-service`, `almacen-service`.

---

## Estructura del proyecto

```
Trabajo_Fullstack/
├── docker-compose.yml
├── api-gateway/
├── auth-service/
├── usuarios-service/
│   └── usuarios-service/
├── pagos-service/
├── pedidos-service/
├── productos-service/
├── almacen-service/
└── envios-service/
```

Cada servicio sigue el patrón **Controller → Service → Repository** con separación clara de capas.
