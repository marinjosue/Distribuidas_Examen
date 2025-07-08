# Test Gateway Routes Script
# Ejecutar este script después de que todos los servicios estén corriendo

Write-Host "🔍 Probando rutas del Gateway API..." -ForegroundColor Green
Write-Host ""

$gatewayUrl = "http://localhost:8000"
$headers = @{
    "Content-Type" = "application/json"
    "Accept" = "application/json"
}

# Función para hacer peticiones HTTP
function Test-Route {
    param (
        [string]$Url,
        [string]$Description,
        [string]$Method = "GET"
    )
    
    Write-Host "📡 Probando: $Description" -ForegroundColor Yellow
    Write-Host "URL: $Url" -ForegroundColor Cyan
    
    try {
        if ($Method -eq "GET") {
            $response = Invoke-WebRequest -Uri $Url -Headers $headers -Method GET -TimeoutSec 10
        } else {
            $response = Invoke-WebRequest -Uri $Url -Headers $headers -Method $Method -TimeoutSec 10
        }
        
        Write-Host "✅ Estado: $($response.StatusCode) - $($response.StatusDescription)" -ForegroundColor Green
        Write-Host "📦 Respuesta: $($response.Content.Length) bytes" -ForegroundColor Gray
        Write-Host ""
        
        return $true
    } catch {
        Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
        return $false
    }
}

# Verificar que el Gateway esté corriendo
Write-Host "🔧 Verificando Gateway..." -ForegroundColor Blue
$gatewayHealthy = Test-Route -Url "$gatewayUrl/actuator/health" -Description "Gateway Health Check"

if (-not $gatewayHealthy) {
    Write-Host "⚠️  Gateway no está disponible. Verificar que esté corriendo en puerto 8000." -ForegroundColor Red
    exit 1
}

# Verificar rutas configuradas
Write-Host "🛣️  Verificando rutas configuradas..." -ForegroundColor Blue
Test-Route -Url "$gatewayUrl/actuator/gateway/routes" -Description "Gateway Routes Configuration"

# Probar rutas de los servicios
Write-Host "🧪 Probando rutas de los servicios..." -ForegroundColor Blue

# Sensor Service
Write-Host "🔬 Probando Sensor Service..." -ForegroundColor Magenta
Test-Route -Url "$gatewayUrl/api/conjunta/2p/sensor-readings/" -Description "Sensor Readings - List All"

# Analyzer Service
Write-Host "📊 Probando Analyzer Service..." -ForegroundColor Magenta
Test-Route -Url "$gatewayUrl/api/conjunta/2p/alerts/" -Description "Alerts - List All"

# Notification Service
Write-Host "📧 Probando Notification Service..." -ForegroundColor Magenta
Test-Route -Url "$gatewayUrl/api/conjunta/2p/notifications/" -Description "Notifications - List All"

Write-Host "✨ Pruebas completadas!" -ForegroundColor Green
Write-Host ""
Write-Host "💡 Notas importantes:" -ForegroundColor Yellow
Write-Host "- Si las rutas fallan, verificar que los servicios estén registrados en Eureka"
Write-Host "- Eureka Dashboard: http://localhost:8761"
Write-Host "- Gateway Health: http://localhost:8000/actuator/health"
Write-Host "- Gateway Routes: http://localhost:8000/actuator/gateway/routes"
