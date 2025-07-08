# Script para depurar las rutas y redirecciones del Gateway
Write-Host "🔍 Verificando rutas del Gateway..." -ForegroundColor Yellow

# 1. Verificar las rutas configuradas
Write-Host "📋 1. Rutas configuradas en el Gateway:" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8000/actuator/gateway/routes" | ConvertTo-Json -Depth 5

# 2. Probar redirección directamente
Write-Host "`n📡 2. Probando redirección directa a los servicios:" -ForegroundColor Green
Write-Host "🔹 Sensor service:" -ForegroundColor Cyan
try {
    $sensor = Invoke-WebRequest -Uri "http://localhost:49974/sensor-readings/" -ErrorAction Stop
    Write-Host "  ✅ Respuesta directa del servicio sensor: $($sensor.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Error al acceder directamente al servicio sensor: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "🔹 Analyzer service:" -ForegroundColor Cyan
try {
    $analyzer = Invoke-WebRequest -Uri "http://localhost:50530/alerts/" -ErrorAction Stop
    Write-Host "  ✅ Respuesta directa del servicio analyzer: $($analyzer.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Error al acceder directamente al servicio analyzer: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "🔹 Notification service:" -ForegroundColor Cyan
try {
    $notification = Invoke-WebRequest -Uri "http://localhost:50747/notifications/" -ErrorAction Stop
    Write-Host "  ✅ Respuesta directa del servicio notification: $($notification.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Error al acceder directamente al servicio notification: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. Probar a través del Gateway
Write-Host "`n📡 3. Probando a través del Gateway:" -ForegroundColor Green
Write-Host "🔹 Sensor service:" -ForegroundColor Cyan
try {
    $gatewaySensor = Invoke-WebRequest -Uri "http://localhost:8000/api/conjunta/2p/sensor-readings/" -ErrorAction Stop
    Write-Host "  ✅ Respuesta a través del gateway al servicio sensor: $($gatewaySensor.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Error al acceder a través del gateway al servicio sensor: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "🔹 Analyzer service:" -ForegroundColor Cyan
try {
    $gatewayAnalyzer = Invoke-WebRequest -Uri "http://localhost:8000/api/conjunta/2p/alerts/" -ErrorAction Stop
    Write-Host "  ✅ Respuesta a través del gateway al servicio analyzer: $($gatewayAnalyzer.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Error al acceder a través del gateway al servicio analyzer: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "🔹 Notification service:" -ForegroundColor Cyan
try {
    $gatewayNotification = Invoke-WebRequest -Uri "http://localhost:8000/api/conjunta/2p/notifications/" -ErrorAction Stop
    Write-Host "  ✅ Respuesta a través del gateway al servicio notification: $($gatewayNotification.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Error al acceder a través del gateway al servicio notification: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. Verificar servicios en Eureka
Write-Host "`n📋 4. Verificando registro en Eureka:" -ForegroundColor Green
Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps" | Select-Object -ExpandProperty applications | Select-Object -ExpandProperty application | ForEach-Object {
    Write-Host "🔹 $($_.name)" -ForegroundColor Cyan
    $_.instance | ForEach-Object {
        Write-Host "  • $($_.instanceId) - Status: $($_.status)"
        Write-Host "    URL: $($_.homePageUrl)"
        Write-Host "    Health: $($_.healthCheckUrl)"
    }
}

Write-Host "`n🔎 Análisis completado. Revise los resultados para identificar el problema." -ForegroundColor Yellow
