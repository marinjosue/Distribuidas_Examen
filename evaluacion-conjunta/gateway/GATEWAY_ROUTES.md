# API Gateway Routes Test

## Configuración corregida

### Rutas configuradas en el Gateway:

#### 1. Sensor Service
- **URL Externa**: `http://localhost:8000/api/conjunta/2p/sensor-readings/**`
- **Redirige a**: `http://sensor-data-collector/sensor-readings/**`
- **Ejemplos**:
  - `GET http://localhost:8000/api/conjunta/2p/sensor-readings/` → Lista todas las lecturas
  - `POST http://localhost:8000/api/conjunta/2p/sensor-readings/` → Crea nueva lectura
  - `GET http://localhost:8000/api/conjunta/2p/sensor-readings/{sensorId}` → Historial de un sensor

#### 2. Analyzer Service
- **URL Externa**: `http://localhost:8000/api/conjunta/2p/alerts/**`
- **Redirige a**: `http://environmental-analyzer/alerts/**`
- **Ejemplos**:
  - `GET http://localhost:8000/api/conjunta/2p/alerts/` → Lista todas las alertas
  - `GET http://localhost:8000/api/conjunta/2p/alerts/sensor/{sensorId}` → Alertas por sensor
  - `GET http://localhost:8000/api/conjunta/2p/alerts/type/{type}` → Alertas por tipo

#### 3. Notification Service
- **URL Externa**: `http://localhost:8000/api/conjunta/2p/notifications/**`
- **Redirige a**: `http://notification-dispatcher/notifications/**`
- **Ejemplos**:
  - `GET http://localhost:8000/api/conjunta/2p/notifications/` → Lista todas las notificaciones
  - `GET http://localhost:8000/api/conjunta/2p/notifications/status/{status}` → Notificaciones por estado
  - `GET http://localhost:8000/api/conjunta/2p/notifications/type/{type}` → Notificaciones por tipo

### Problemas corregidos:

1. **Configuración YAML incorrecta**: Se corrigió la estructura del archivo application.yml del Gateway
2. **Errores tipográficos**: Se corrigió `prefer-ip-addres` → `prefer-ip-address` en el servicio sensor
3. **Configuración de Eureka**: Se agregó `metadata-map` para separar las rutas de management de las rutas de API
4. **Management endpoints**: Se configuró `base-path: /actuator` para evitar conflictos con las rutas de API
5. **Instance ID**: Se agregó `instance-id` único para cada servicio con `${random.value}`

### Cómo probar:

1. Iniciar Eureka Server (puerto 8761)
2. Iniciar los servicios (sensor, analyzer, notification)
3. Iniciar el Gateway (puerto 8000)
4. Verificar en Eureka Dashboard que todos los servicios estén registrados
5. Probar las rutas través del Gateway

### Ejemplo de prueba con curl:

```bash
# Probar sensor readings
curl -X GET http://localhost:8000/api/conjunta/2p/sensor-readings/

# Probar alerts
curl -X GET http://localhost:8000/api/conjunta/2p/alerts/

# Probar notifications
curl -X GET http://localhost:8000/api/conjunta/2p/notifications/
```

### Verificar rutas del Gateway:

```bash
# Ver rutas configuradas
curl -X GET http://localhost:8000/actuator/gateway/routes
```
