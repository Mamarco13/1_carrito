#!/bin/bash
# Levanta solo PostgreSQL en Docker (para desarrollo local con ./mvnw spring-boot:run)

echo "🐘 Iniciando PostgreSQL en Docker..."

# Verificar si Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker no está corriendo. Inicia Docker Desktop primero."
    exit 1
fi

# Verificar si el contenedor ya existe
if docker ps --filter "name=carrito-postgres" --format "{{.Names}}" | grep -q "carrito-postgres"; then
    echo "✅ PostgreSQL ya está corriendo"
    exit 0
fi

if docker ps -a --filter "name=carrito-postgres" --format "{{.Names}}" | grep -q "carrito-postgres"; then
    echo "🔄 Reiniciando contenedor existente..."
    docker start carrito-postgres
    exit 0
fi

# Crear y arrancar nuevo contenedor
echo "📦 Creando contenedor PostgreSQL..."
docker run -d \
    --name carrito-postgres \
    -e POSTGRES_DB=carrito_db \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \
    -p 5432:5432 \
    -v postgres_data:/var/lib/postgresql/data \
    postgres:16-alpine

echo "⏳ Esperando a que PostgreSQL esté listo..."
sleep 5

echo "✅ PostgreSQL listo en localhost:5432"
echo "   Usuario: postgres"
echo "   Contraseña: postgres"
echo "   Base de datos: carrito_db"
echo ""
echo "🚀 Ahora ejecuta: ./mvnw spring-boot:run"