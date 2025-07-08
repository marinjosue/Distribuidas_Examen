# Script para reiniciar todos los servicios en el orden correcto
Write-Host "🚀 Reiniciando todos los servicios..." -ForegroundColor Yellow

# Directorio base
$baseDir = "C:\Desktop\ESPE\6-7 level\Distribuidas\evaluacion-conjunta"

# Función para detener procesos Java
function Stop-JavaProcesses {
    param (
        [string]$ServiceName
    )
    
    Write-Host "⏹️ Deteniendo servicio: $ServiceName" -ForegroundColor Gray
    
    $javaPids = Get-Process -Name java -ErrorAction SilentlyContinue | 
                Where-Object { $_.CommandLine -like "*$ServiceName*" } | 
                Select-Object -ExpandProperty Id
                
    if ($javaPids) {
        foreach ($pid in $javaPids) {
            Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
            Write-Host "   ✓ Proceso Java con PID $pid detenido" -ForegroundColor DarkGray
        }
    } else {
        Write-Host "   ℹ️ No se encontraron procesos Java para $ServiceName" -ForegroundColor DarkGray
    }
}

# Detener todos los servicios
Write-Host "`n🛑 Deteniendo todos los servicios existentes..." -ForegroundColor Red
Stop-JavaProcesses -ServiceName "eureka"
Stop-JavaProcesses -ServiceName "gateway"
Stop-JavaProcesses -ServiceName "sensor"
Stop-JavaProcesses -ServiceName "analyzer"
Stop-JavaProcesses -ServiceName "notification"
Start-Sleep -Seconds 2

# Iniciar servicios en el orden correcto
Write-Host "`n🟢 Iniciando servicios en orden correcto..." -ForegroundColor Green

# 1. Eureka Server
Write-Host "`n🔷 1. Iniciando Eureka Server..." -ForegroundColor Cyan
$eurekaProcess = Start-Process -FilePath "powershell" -ArgumentList "-Command", "cd '$baseDir\eureka' ; mvn spring-boot:run" -PassThru -WindowStyle Minimized
Write-Host "   ✅ Eureka Server iniciado - PID: $($eurekaProcess.Id)" -ForegroundColor Green
Write-Host "   ⏳ Esperando 15 segundos para que Eureka inicie completamente..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

# 2. Gateway
Write-Host "`n🔷 2. Iniciando API Gateway..." -ForegroundColor Cyan
$gatewayProcess = Start-Process -FilePath "powershell" -ArgumentList "-Command", "cd '$baseDir\gateway' ; mvn spring-boot:run" -PassThru -WindowStyle Minimized
Write-Host "   ✅ API Gateway iniciado - PID: $($gatewayProcess.Id)" -ForegroundColor Green
Write-Host "   ⏳ Esperando 10 segundos para que Gateway inicie..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# 3. Sensor Service
Write-Host "`n🔷 3. Iniciando Sensor Service..." -ForegroundColor Cyan
$sensorProcess = Start-Process -FilePath "powershell" -ArgumentList "-Command", "cd '$baseDir\sensor' ; mvn spring-boot:run" -PassThru -WindowStyle Minimized
Write-Host "   ✅ Sensor Service iniciado - PID: $($sensorProcess.Id)" -ForegroundColor Green
Write-Host "   ⏳ Esperando 10 segundos..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# 4. Analyzer Service
Write-Host "`n🔷 4. Iniciando Analyzer Service..." -ForegroundColor Cyan
$analyzerProcess = Start-Process -FilePath "powershell" -ArgumentList "-Command", "cd '$baseDir\analyzer' ; mvn spring-boot:run" -PassThru -WindowStyle Minimized
Write-Host "   ✅ Analyzer Service iniciado - PID: $($analyzerProcess.Id)" -ForegroundColor Green
Write-Host "   ⏳ Esperando 10 segundos..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# 5. Notification Service
Write-Host "`n🔷 5. Iniciando Notification Service..." -ForegroundColor Cyan
$notificationProcess = Start-Process -FilePath "powershell" -ArgumentList "-Command", "cd '$baseDir\notification' ; mvn spring-boot:run" -PassThru -WindowStyle Minimized
Write-Host "   ✅ Notification Service iniciado - PID: $($notificationProcess.Id)" -ForegroundColor Green
Write-Host "   ⏳ Esperando 10 segundos para que todos los servicios se registren..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Verificar estado de Eureka
Write-Host "`n🔍 Verificando registro en Eureka..." -ForegroundColor Yellow
try {
    $eurekaApps = Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps"
    $services = $eurekaApps.applications.application
    
    Write-Host "`n📋 Servicios registrados en Eureka:" -ForegroundColor Green
    foreach ($service in $services) {
        $status = $service.instance.status
        $statusColor = if ($status -eq "UP") { "Green" } else { "Red" }
        Write-Host "   • $($service.name) - " -NoNewline
        Write-Host "Status: $status" -ForegroundColor $statusColor
        Write-Host "     URL: $($service.instance.homePageUrl)"
    }
} catch {
    Write-Host "   ❌ Error al consultar Eureka: $($_.Exception.Message)" -ForegroundColor Red
}

# Mostrar instrucciones
Write-Host "`n📝 Instrucciones de prueba:" -ForegroundColor Yellow
Write-Host "   • Eureka Dashboard: http://localhost:8761"
Write-Host "   • Gateway Routes: http://localhost:8000/actuator/gateway/routes"
Write-Host "   • API Sensor: http://localhost:8000/api/conjunta/2p/sensor-readings/"
Write-Host "   • API Analyzer: http://localhost:8000/api/conjunta/2p/alerts/"
Write-Host "   • API Notification: http://localhost:8000/api/conjunta/2p/notifications/"

Write-Host "`n✨ ¡Todos los servicios reiniciados!" -ForegroundColor Magenta
Write-Host "   Para probar la conectividad, ejecute: .\debug-gateway.ps1" -ForegroundColor Yellow
