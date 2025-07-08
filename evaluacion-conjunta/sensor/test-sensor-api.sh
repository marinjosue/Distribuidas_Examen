#!/bin/bash

# Script para probar el microservicio de sensores
# Asegúrate de que el microservicio esté ejecutándose en el puerto 8081

BASE_URL="http://localhost:8081/api/conjunta/2p"
CONTENT_TYPE="Content-Type: application/json"

echo "=== Pruebas del Microservicio de Sensores ==="
echo ""

# Función para hacer peticiones POST
test_post() {
    local sensor_id=$1
    local type=$2
    local value=$3
    local description=$4
    
    echo "Probando: $description"
    echo "POST $BASE_URL/sensor-readings"
    
    response=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/sensor-readings" \
        -H "$CONTENT_TYPE" \
        -d "{\"sensor_id\":\"$sensor_id\",\"type\":\"$type\",\"value\":$value}")
    
    if [ "$response" -eq 201 ]; then
        echo "✅ SUCCESS: $description (HTTP $response)"
    else
        echo "❌ FAILED: $description (HTTP $response)"
    fi
    echo ""
}

# Función para hacer peticiones GET
test_get() {
    local endpoint=$1
    local description=$2
    
    echo "Probando: $description"
    echo "GET $BASE_URL$endpoint"
    
    response=$(curl -s -o /dev/null -w "%{http_code}" -X GET "$BASE_URL$endpoint")
    
    if [ "$response" -eq 200 ] || [ "$response" -eq 204 ]; then
        echo "✅ SUCCESS: $description (HTTP $response)"
    else
        echo "❌ FAILED: $description (HTTP $response)"
    fi
    echo ""
}

# Verificar que el servicio está disponible
echo "Verificando disponibilidad del servicio..."
health_check=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/actuator/health")
if [ "$health_check" -eq 200 ]; then
    echo "✅ Servicio disponible"
else
    echo "❌ Servicio no disponible. Verifica que esté ejecutándose."
    exit 1
fi
echo ""

# Pruebas POST - Crear lecturas de sensores
echo "=== PRUEBAS POST /sensor-readings ==="
test_post "TEMP001" "temperature" "23.5" "Sensor de temperatura"
test_post "HUM001" "humidity" "65.0" "Sensor de humedad"
test_post "PRESS001" "pressure" "1013.25" "Sensor de presión"
test_post "TEMP002" "temperature" "25.3" "Segundo sensor de temperatura"
test_post "TEMP001" "temperature" "24.1" "Segunda lectura del primer sensor"

# Pruebas GET - Consultar lecturas
echo "=== PRUEBAS GET /sensor-readings ==="
test_get "/sensor-readings" "Obtener todas las lecturas"
test_get "/sensor-readings/TEMP001" "Obtener historial de TEMP001"
test_get "/sensor-readings/HUM001" "Obtener historial de HUM001"
test_get "/sensor-readings/PRESS001" "Obtener historial de PRESS001"
test_get "/sensor-readings/NONEXISTENT" "Obtener historial de sensor inexistente"

echo "=== PRUEBAS GET ENDPOINTS ADICIONALES ==="
test_get "/sensor-readings/TEMP001/latest" "Obtener última lectura de TEMP001"
test_get "/sensor-readings/TEMP001/count" "Contar lecturas de TEMP001"
test_get "/sensor-readings/type/temperature" "Obtener lecturas por tipo 'temperature'"

echo "=== PRUEBAS COMPLETADAS ==="
echo ""
echo "Para ver las respuestas detalladas, ejecuta:"
echo "curl -X GET \"$BASE_URL/sensor-readings\" | jq"
echo ""
echo "Para probar manualmente:"
echo "curl -X POST \"$BASE_URL/sensor-readings\" -H \"$CONTENT_TYPE\" -d '{\"sensor_id\":\"TEST001\",\"type\":\"temperature\",\"value\":22.5}'"
