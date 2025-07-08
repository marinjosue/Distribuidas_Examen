# Ejemplos de Uso - API de Microservicios

## Endpoints Disponibles

Todos los endpoints están disponibles a través del API Gateway en `http://localhost:8080/api/conjunta/2p/`

## 1. Sensor Data Collector

### Enviar Lectura de Temperatura (Crítica)
```bash
curl -X POST http://localhost:8080/api/conjunta/2p/sensor-readings \
  -H "Content-Type: application/json" \
  -d '{
    "sensorId": "S001",
    "type": "temperature", 
    "value": 45.0,
    "timestamp": "2024-04-05T12:00:00Z"
  }'
```

### Enviar Lectura de Humedad (Baja)
```bash
curl -X POST http://localhost:8080/api/conjunta/2p/sensor-readings \
  -H "Content-Type: application/json" \
  -d '{
    "sensorId": "S002",
    "type": "humidity",
    "value": 15.0,
    "timestamp": "2024-04-05T12:05:00Z"
  }'
```

### Enviar Lectura Sísmica (Crítica)
```bash
curl -X POST http://localhost:8080/api/conjunta/2p/sensor-readings \
  -H "Content-Type: application/json" \
  -d '{
    "sensorId": "S003",
    "type": "seismic",
    "value": 4.2,
    "timestamp": "2024-04-05T12:10:00Z"
  }'
```

### Obtener Historial de un Sensor
```bash
curl http://localhost:8080/api/conjunta/2p/sensor-readings/S001
```

### Obtener Todas las Lecturas
```bash
curl http://localhost:8080/api/conjunta/2p/sensor-readings
```

## 2. Environmental Analyzer

### Ver Todas las Alertas
```bash
curl http://localhost:8080/api/conjunta/2p/alerts
```

### Ver Alertas Críticas
```bash
curl http://localhost:8080/api/conjunta/2p/alerts/critical
```

### Ver Alertas por Sensor
```bash
curl http://localhost:8080/api/conjunta/2p/alerts/sensor/S001
```

### Ver Alertas por Tipo
```bash
curl http://localhost:8080/api/conjunta/2p/alerts/type/HighTemperatureAlert
```

### Ver Alertas por Severidad
```bash
curl http://localhost:8080/api/conjunta/2p/alerts/severity/CRITICAL
```

## 3. Notification Dispatcher

### Ver Todas las Notificaciones
```bash
curl http://localhost:8080/api/conjunta/2p/notifications
```

### Ver Notificaciones Enviadas
```bash
curl http://localhost:8080/api/conjunta/2p/notifications/status/SENT
```

### Ver Notificaciones Pendientes
```bash
curl http://localhost:8080/api/conjunta/2p/notifications/status/PENDING
```

### Ver Notificaciones por Tipo
```bash
curl http://localhost:8080/api/conjunta/2p/notifications/type/EMAIL
```

## Flujo Completo de Ejemplo

### 1. Enviar múltiples lecturas para generar alertas
```bash
# Temperatura crítica
curl -X POST http://localhost:8080/api/conjunta/2p/sensor-readings \
  -H "Content-Type: application/json" \
  -d '{"sensorId": "TEMP001", "type": "temperature", "value": 50.0}'

# Humedad baja  
curl -X POST http://localhost:8080/api/conjunta/2p/sensor-readings \
  -H "Content-Type: application/json" \
  -d '{"sensorId": "HUM001", "type": "humidity", "value": 10.0}'

# Actividad sísmica
curl -X POST http://localhost:8080/api/conjunta/2p/sensor-readings \
  -H "Content-Type: application/json" \
  -d '{"sensorId": "SEISM001", "type": "seismic", "value": 5.5}'
```

### 2. Verificar alertas generadas
```bash
curl http://localhost:8080/api/conjunta/2p/alerts/critical
```

### 3. Verificar notificaciones enviadas
```bash
curl http://localhost:8080/api/conjunta/2p/notifications/status/SENT
```

## Monitoreo en Tiempo Real

### Ver logs del sistema completo
```bash
docker-compose logs -f
```

### Ver logs de un servicio específico
```bash
docker-compose logs -f sensor-service
docker-compose logs -f analyzer-service 
docker-compose logs -f notification-service
```

### Verificar estado de servicios
```bash
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/actuator/health  # Sensor Service
curl http://localhost:8082/actuator/health  # Analyzer Service  
curl http://localhost:8083/actuator/health  # Notification Service
```

## Dashboards Web

- **Eureka Discovery**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672 (admin/admin)
- **CockroachDB Dashboard**: http://localhost:8080

## Patrón de Eventos Esperado

1. **Lectura de Sensor** → `NewSensorReadingEvent` → **Analyzer**
2. **Análisis** → `AlertEvent` (si aplica) → **Notification**  
3. **Notificación** → Email/SMS/Push simulados

## Testing de Resiliencia

### Simular falla de RabbitMQ
```bash
docker-compose stop rabbitmq
# Enviar lecturas - se almacenarán localmente
curl -X POST http://localhost:8080/api/conjunta/2p/sensor-readings \
  -H "Content-Type: application/json" \
  -d '{"sensorId": "TEST", "type": "temperature", "value": 55.0}'

# Restaurar RabbitMQ - eventos se procesarán automáticamente
docker-compose start rabbitmq
```

### Verificar recuperación automática
```bash
# Ver logs del sensor service para confirmar procesamiento de eventos pendientes
docker-compose logs -f sensor-service
```
