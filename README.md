# Carrito App - Spring Boot + PostgreSQL

Aplicación web de carrito de compras desarrollada con **Spring Boot 4.1**, **Thymeleaf**, **Spring Security** y **PostgreSQL**.

---

## 🚀 Despliegue Rápido (Recomendado para el profesor)

### Opción 1: Docker Compose (Todo en uno) ⭐ **RECOMENDADO**
```bash
# Linux/Mac
./deploy.sh

# Windows (PowerShell)
.\deploy.ps1
```
Esto levanta **PostgreSQL + App** en contenedores. Acceso: **http://localhost:8080**

---

### Opción 2: Solo Base de Datos (Desarrollo local)
```bash
# Linux/Mac
./start-db.sh

# Windows (PowerShell)
.\start-db.ps1
```
Luego ejecuta la app localmente:
```bash
./mvnw spring-boot:run
```

> ⚠️ **Nota importante para Windows**: Existe una limitación conocida en Docker Desktop para Windows con el reenvío de puertos (`localhost:5432` → contenedor). La autenticación `trust` en `pg_hba.conf` no coincide correctamente con las conexiones que pasan por el proxy de Docker Desktop.
>
> **Alternativas para desarrollo local en Windows:**
> 1. **Usar Docker Compose completo** (Opción 1) - Funciona perfectamente
> 2. **Instalar PostgreSQL nativamente en Windows** - Descargar de postgresql.org
> 3. **Usar PostgreSQL en la nube** (Neon, Supabase, Railway - gratuitos)
> 4. **WSL2 + Docker** - Mejor compatibilidad de red

---

## 📋 Requisitos Previos

- **Docker Desktop** instalado y corriendo
- **Java 17+** (solo para desarrollo local sin Docker)
- **Maven** (incluido como wrapper `mvnw`)

---

## 🔐 Credenciales por Defecto

| Servicio | Usuario | Contraseña |
|----------|---------|------------|
| App (Spring Security) | admin | admin |
| PostgreSQL | postgres | postgres |
| Base de datos | carrito_db | - |

---

## 🐳 Servicios Docker

| Contenedor | Puerto | Descripción |
|------------|--------|-------------|
| `carrito-postgres` | 5432 | Base de datos PostgreSQL 16 |
| `carrito-app` | 8080 | Aplicación Spring Boot |

---

## 🛠 Comandos Útiles

```bash
# Ver logs de la app
docker-compose logs -f app

# Ver logs de PostgreSQL
docker-compose logs -f postgres

# Parar todo
docker-compose down

# Parar y borrar volúmenes (reset total BD)
docker-compose down -v

# Reconstruir app tras cambios de código
docker-compose build app && docker-compose up -d app
```

---

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/practicas/__carrito/
│   │   ├── controller/     # Controladores Web (Thymeleaf)
│   │   ├── model/          # Entidades JPA (Product)
│   │   ├── repository/     # Spring Data JPA Repositories
│   │   ├── service/        # Lógica de negocio
│   │   └── config/         # Seguridad
│   └── resources/
│       ├── templates/      # Vistas Thymeleaf
│       └── application.properties
├── test/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── deploy.sh / deploy.ps1
└── start-db.sh / start-db.ps1
```

---

## 🧪 Endpoints Principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/` | Página principal (redirige a login) |
| GET | `/login` | Formulario de login |
| GET | `/products` | Listado de productos (requiere login) |
| GET | `/products/new` | Formulario nuevo producto |
| POST | `/products` | Guardar producto |
| GET | `/products/edit/{id}` | Editar producto |
| GET | `/products/delete/{id}` | Eliminar producto |
| GET | `/cart` | Ver carrito |
| POST | `/cart/add/{id}` | Añadir al carrito |
| POST | `/cart/remove/{id}` | Quitar del carrito |

---

## 📦 Build Manual (sin Docker)

```bash
# Compilar
./mvnw clean package

# Ejecutar JAR
java -jar target/1_carrito-0.0.1-SNAPSHOT.jar
```

---

## 🌐 Variables de Entorno (Producción)

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5432/carrito_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario BD | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña BD | `postgres` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estrategia DDL | `update` |

---

## 📝 Notas para el Profesor

1. **Todo está containerizado**: Solo necesita Docker Desktop instalado
2. **Un solo comando**: `./deploy.sh` o `.\deploy.ps1` levanta todo
3. **Datos persistentes**: PostgreSQL usa volume `postgres_data` (sobrevive a reinicios)
4. **Healthchecks**: La app espera a que PostgreSQL esté listo antes de arrancar
5. **Multiplataforma**: Scripts para Windows (`.ps1`) y Linux/Mac (`.sh`)

---

## 🐛 Troubleshooting

**Puerto 8080 ocupado:**
```bash
# Cambiar puerto en docker-compose.yml: "8081:8080"
```

**Error de conexión a BD:**
```bash
# Verificar que PostgreSQL está healthy
docker-compose ps
docker-compose logs postgres
```

**Reinicio limpio total:**
```bash
docker-compose down -v && ./deploy.sh
```

**Desarrollo local en Windows (./mvnw spring-boot:run):**
```bash
# Si falla la conexión a PostgreSQL en localhost:5432:
# 1. Usar Docker Compose completo (recomendado): .\deploy.ps1
# 2. O instalar PostgreSQL nativo en Windows
# 3. O configurar BD en la nube (Neon, Supabase - gratis)
```