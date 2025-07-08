# Script PowerShell para probar el microservicio de sensores
# Asegúrate de que el microservicio esté ejecutándose

$BaseUrl = "http://localhost:8081/api/conjunta/2p"
$ContentType = "application/json"

Write-Host "=== Pruebas del Microservicio de Sensores ===" -ForegroundColor Green
Write-Host ""

# Función para hacer peticiones POST
function Test-Post {
    param(
        [string]$SensorId,
        [string]$Type,
        [double]$Value,
        [string]$Description
    )
    
    Write-Host "Probando: $Description" -ForegroundColor Yellow
    Write-Host "POST $BaseUrl/sensor-readings"
    
    $body = @{
        sensor_id = $SensorId
        type = $Type
        value = $Value
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/sensor-readings" -Method Post -Body $body -ContentType $ContentType
        Write-Host "✅ SUCCESS: $Description" -ForegroundColor Green
        Write-Host "Response: $($response | ConvertTo-Json -Compress)" -ForegroundColor Cyan
    }
    catch {
        Write-Host "❌ FAILED: $Description" -ForegroundColor Red
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

# Función para hacer peticiones GET
function Test-Get {
    param(
        [string]$Endpoint,
        [string]$Description
    )
    
    Write-Host "Probando: $Description" -ForegroundColor Yellow
    Write-Host "GET $BaseUrl$Endpoint"
    
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl$Endpoint" -Method Get
        Write-Host "✅ SUCCESS: $Description" -ForegroundColor Green
        if ($response -is [array]) {
            Write-Host "Response: $($response.Count) items returned" -ForegroundColor Cyan
        } else {
            Write-Host "Response: $($response | ConvertTo-Json -Compress)" -ForegroundColor Cyan
        }
    }
    catch {
        if ($_.Exception.Response.StatusCode -eq 204) {
            Write-Host "✅ SUCCESS: $Description (No Content)" -ForegroundColor Green
        } else {
            Write-Host "❌ FAILED: $Description" -ForegroundColor Red
            Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
    Write-Host ""
}

# Verificar que el servicio está disponible
Write-Host "Verificando disponibilidad del servicio..." -ForegroundColor Yellow
try {
    $healthCheck = Invoke-RestMethod -Uri "$BaseUrl/actuator/health" -Method Get
    Write-Host "✅ Servicio disponible" -ForegroundColor Green
}
catch {
    Write-Host "❌ Servicio no disponible. Verifica que esté ejecutándose." -ForegroundColor Red
    exit 1
}
Write-Host ""

# Pruebas POST - Crear lecturas de sensores
Write-Host "=== PRUEBAS POST /sensor-readings ===" -ForegroundColor Blue
Test-Post -SensorId "TEMP001" -Type "temperature" -Value 23.5 -Description "Sensor de temperatura"
Test-Post -SensorId "HUM001" -Type "humidity" -Value 65.0 -Description "Sensor de humedad"
Test-Post -SensorId "PRESS001" -Type "pressure" -Value 1013.25 -Description "Sensor de presión"
Test-Post -SensorId "TEMP002" -Type "temperature" -Value 25.3 -Description "Segundo sensor de temperatura"
Test-Post -SensorId "TEMP001" -Type "temperature" -Value 24.1 -Description "Segunda lectura del primer sensor"

# Pruebas GET - Consultar lecturas
Write-Host "=== PRUEBAS GET /sensor-readings ===" -ForegroundColor Blue
Test-Get -Endpoint "/sensor-readings" -Description "Obtener todas las lecturas"
Test-Get -Endpoint "/sensor-readings/TEMP001" -Description "Obtener historial de TEMP001"
Test-Get -Endpoint "/sensor-readings/HUM001" -Description "Obtener historial de HUM001"
Test-Get -Endpoint "/sensor-readings/PRESS001" -Description "Obtener historial de PRESS001"
Test-Get -Endpoint "/sensor-readings/NONEXISTENT" -Description "Obtener historial de sensor inexistente"

Write-Host "=== PRUEBAS GET ENDPOINTS ADICIONALES ===" -ForegroundColor Blue
Test-Get -Endpoint "/sensor-readings/TEMP001/latest" -Description "Obtener última lectura de TEMP001"
Test-Get -Endpoint "/sensor-readings/TEMP001/count" -Description "Contar lecturas de TEMP001"
Test-Get -Endpoint "/sensor-readings/type/temperature" -Description "Obtener lecturas por tipo 'temperature'"

Write-Host "=== PRUEBAS COMPLETADAS ===" -ForegroundColor Green
Write-Host ""
Write-Host "Para probar manualmente:" -ForegroundColor Yellow
Write-Host "Invoke-RestMethod -Uri '$BaseUrl/sensor-readings' -Method Get | ConvertTo-Json" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para crear una nueva lectura:" -ForegroundColor Yellow
Write-Host "Invoke-RestMethod -Uri '$BaseUrl/sensor-readings' -Method Post -Body (ConvertTo-Json @{sensor_id='TEST001';type='temperature';value=22.5}) -ContentType 'application/json'" -ForegroundColor Cyan
