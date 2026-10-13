<# 
.SYNOPSIS
    Levanta solo PostgreSQL en Docker (para desarrollo local con ./mvnw spring-boot:run)
.EXAMPLE
    .\start-db.ps1
#>

# Función para configurar pg_hba.conf
function ConfigurePgHba {
    Write-Host "Configurando autenticación para conexiones externas..." -ForegroundColor Cyan
    # Permitir conexiones desde la red de Docker (gateway) con password
    docker exec carrito-postgres sh -c "echo 'host all all 172.16.0.0/12 scram-sha-256' >> /var/lib/postgresql/data/pg_hba.conf"
    docker exec carrito-postgres sh -c "echo 'host all all 10.0.0.0/8 scram-sha-256' >> /var/lib/postgresql/data/pg_hba.conf"
    # Recargar configuración
    docker exec carrito-postgres psql -U postgres -d carrito_db -c "SELECT pg_reload_conf();" | Out-Null
    Write-Host "Configuración aplicada" -ForegroundColor Green
}

Write-Host "Iniciando PostgreSQL en Docker..." -ForegroundColor Green

# Verificar si Docker esta corriendo
try {
    docker info | Out-Null
} catch {
    Write-Host "Docker no esta corriendo. Inicia Docker Desktop primero." -ForegroundColor Red
    exit 1
}

# Verificar si el contenedor ya existe
$container = docker ps -a --filter "name=carrito-postgres" --format "{{.Names}}"
if ($container) {
    $status = docker inspect -f '{{.State.Status}}' carrito-postgres
    if ($status -eq "running") {
        Write-Host "PostgreSQL ya esta corriendo" -ForegroundColor Green
        exit 0
    } elseif ($status -eq "exited") {
        Write-Host "Reiniciando contenedor existente..." -ForegroundColor Yellow
        docker start carrito-postgres
        # Esperar y configurar pg_hba.conf
        Start-Sleep -Seconds 3
        ConfigurePgHba
        exit 0
    }
}

# Crear y arrancar nuevo contenedor
Write-Host "Creando contenedor PostgreSQL..." -ForegroundColor Cyan
docker run -d `
    --name carrito-postgres `
    -e POSTGRES_DB=carrito_db `
    -e POSTGRES_USER=postgres `
    -e POSTGRES_PASSWORD=postgres `
    -p 5432:5432 `
    -v postgres_data:/var/lib/postgresql/data `
    postgres:16-alpine

Write-Host "Esperando a que PostgreSQL este listo..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# Configurar pg_hba.conf para permitir conexiones desde el host (gateway de Docker)
ConfigurePgHba

Write-Host "PostgreSQL listo en localhost:5432" -ForegroundColor Green
Write-Host "  Usuario: postgres" -ForegroundColor Cyan
Write-Host "  Contraseña: postgres" -ForegroundColor Cyan
Write-Host "  Base de datos: carrito_db" -ForegroundColor Cyan
Write-Host ""
Write-Host "Ahora ejecuta: ./mvnw spring-boot:run" -ForegroundColor Green