#!/bin/bash
# Script de despliegue para Linux/Mac
# Uso: ./deploy.sh

set -e

echo "Desplegando Carrito App con Docker Compose..."

# Verificar si Docker esta corriendo
if ! docker info > /dev/null 2>&1; then
    echo "Docker no esta corriendo. Inicia Docker Desktop primero."
    exit 1
fi

# Detener contenedores previos si existen (incluye contenedores sueltos)
echo "Limpiando contenedores anteriores..."
docker rm -f carrito-postgres carrito-app 2>/dev/null || true
docker-compose down 2>/dev/null || true

# Construir y levantar
echo "Construyendo imagen de la aplicacion..."
docker-compose build

echo "Levantando servicios..."
docker-compose up -d

# Esperar a que la app este lista
echo "Esperando a que la aplicacion este lista..."
maxAttempts=60
for i in $(seq 1 $maxAttempts); do
    if curl -s -f http://localhost:8080 > /dev/null 2>&1; then
        echo ""
        echo "Aplicacion desplegada correctamente!"
        echo ""
        echo "Informacion de acceso:"
        echo "  Aplicacion: http://localhost:8080"
        echo "  PostgreSQL: localhost:5432 (usuario: postgres, pass: postgres, db: carrito_db)"
        echo ""
        echo "Para ver logs: docker-compose logs -f"
        echo "Para parar: docker-compose down"
        exit 0
    fi
    if [ $((i % 10)) -eq 0 ]; then
        echo "  ... esperando ($i/$maxAttempts)"
    fi
    sleep 2
done

echo "La aplicacion no respondio a tiempo. Ver logs con: docker-compose logs app"
exit 1