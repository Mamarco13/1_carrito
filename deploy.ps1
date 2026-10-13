<# 
.SYNOPSIS
    Script de despliegue para Carrito App en Windows (PowerShell)
.DESCRIPTION
    Levanta PostgreSQL y la aplicacion Spring Boot usando Docker Compose
.EXAMPLE
    .\deploy.ps1
#>

Write-Host "Desplegando Carrito App con Docker Compose..."

# Verificar si Docker esta corriendo
try {
    docker info | Out-Null
} catch {
    Write-Host "Docker no esta corriendo. Inicia Docker Desktop primero." -ForegroundColor Red
    exit 1
}

# Detener contenedores previos si existen (incluye contenedores sueltos)
Write-Host "Limpiando contenedores anteriores..." -ForegroundColor Yellow
docker rm -f carrito-postgres carrito-app 2>$null | Out-Null
docker-compose down 2>$null | Out-Null

# Construir y levantar
Write-Host "Construyendo imagen de la aplicacion..." -ForegroundColor Cyan
docker-compose build

Write-Host "Levantando servicios..." -ForegroundColor Cyan
docker-compose up -d

# Esperar a que la app este lista
Write-Host "Esperando a que la aplicacion este lista..." -ForegroundColor Yellow
$maxAttempts = 60
$success = $false

for ($i = 1; $i -le $maxAttempts; $i++) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080" -Method Get -UseBasicParsing -TimeoutSec 3 -ErrorAction SilentlyContinue
        if ($null -ne $response -and $response.StatusCode -eq 200) {
            $success = $true
            break
        }
    } catch {
        # Ignorar errores, seguir esperando
    }
    if ($i % 10 -eq 0) {
        Write-Host "   ... esperando ($i/$maxAttempts)" -ForegroundColor Gray
    }
    Start-Sleep -Seconds 2
}

if ($success) {
    Write-Host ""
    Write-Host "Aplicacion desplegada correctamente!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Informacion de acceso:" -ForegroundColor Cyan
    Write-Host "  Aplicacion: http://localhost:8080"
    Write-Host "  PostgreSQL: localhost:5432 (usuario: postgres, pass: postgres, db: carrito_db)"
    Write-Host ""
    Write-Host "Para ver logs: docker-compose logs -f"
    Write-Host "Para parar: docker-compose down"
    exit 0
} else {
    Write-Host ""
    Write-Host "La aplicacion no respondio a tiempo. Ver logs con: docker-compose logs app" -ForegroundColor Red
    exit 1
}