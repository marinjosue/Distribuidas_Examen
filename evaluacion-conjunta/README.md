# Evaluación Conjunta - Arquitectura de Microservicios Distribuidos

## Descripción del Proyecto

Este proyecto implementa una arquitectura completa de microservicios distribuidos para el análisis de datos de sensores ambientales, cumpliendo con todos los requisitos especificados:

### Arquitectura Implementada

1. **API Gateway** - Gestión centralizada de rutas (Puerto 8080)
2. **Eureka Server** - Descubrimiento de servicios (Puerto 8761)
3. **RabbitMQ** - Comunicación asíncrona entre microservicios
4. **CockroachDB** - Persistencia distribuida con replicación geográfica
5. **Microservicios**:
   - **SensorDataCollector** (Puerto 8081)
   - **EnvironmentalAnalyzer** (Puerto 8082)
   - **NotificationDispatcher** (Puerto 8083)

## Tecnologías Utilizadas

- **Spring Boot 3.5.3**
- **Spring Cloud 2025.0.0**
- **Spring Data JPA**
- **Spring AMQP (RabbitMQ)**
- **Netflix Eureka**
- **Spring Cloud Gateway**
- **CockroachDB/PostgreSQL**
- **H2 Database** (para desarrollo local)
- **Docker & Docker Compose**
- **Maven**

## Microservicios Implementados

### 1. SensorDataCollector (Puerto 8081)

**Propósito**: Recibir y almacenar datos brutos de sensores ambientales.

**Endpoints**:
- `POST /sensor-readings` - Recibe datos de sensores
- `GET /sensor-readings/{sensorId}` - Historial de un sensor
- `GET /sensor-readings` - Todas las lecturas

**Características**:
- Validación de rangos por tipo de sensor
- Persistencia en CockroachDB
- Emisión de eventos `NewSensorReadingEvent`
- Resiliencia con almacenamiento local (SQLite)
- Reintentos con retroceso exponencial

### 2. EnvironmentalAnalyzer (Puerto 8082)

**Propósito**: Analizar datos de sensores y generar alertas.

**Endpoints**:
- `GET /alerts` - Todas las alertas
- `GET /alerts/sensor/{sensorId}` - Alertas por sensor
- `GET /alerts/type/{type}` - Alertas por tipo
- `GET /alerts/severity/{severity}` - Alertas por severidad
- `GET /alerts/critical` - Alertas críticas

**Lógica de Análisis**:
- **Temperatura alta**: > 40°C → `HighTemperatureAlert`
- **Humedad baja**: < 20% → `LowHumidityWarning`
- **Actividad sísmica**: > 3.0 → `SeismicActivityDetected`

**Tareas Programadas**:
- Reportes diarios (00:00)
- Verificación de sensores inactivos (cada 6 horas)

### 3. NotificationDispatcher (Puerto 8083)

**Propósito**: Enviar notificaciones basadas en eventos críticos.

**Endpoints**:
- `GET /notifications` - Todas las notificaciones
- `GET /notifications/status/{status}` - Por estado
- `GET /notifications/type/{type}` - Por tipo
- `POST /mock-email` - Endpoint simulado de email
- `POST /mock-sms` - Endpoint simulado de SMS

**Tipos de Notificaciones**:
- **Email** (simulado con logs)
- **SMS** (simulado con logs)
- **Push Notifications** (simulado en consola)

**Priorización**:
- **CRITICAL**: Envío inmediato
- **WARNING/INFO**: Envío en lotes cada 30 minutos

## Configuración y Despliegue

### Prerrequisitos

- Java 17+
- Maven 3.6+
- Docker & Docker Compose

### Ejecución con Docker Compose

1. **Clonar el repositorio y navegar al directorio**:
```bash
cd evaluacion-conjunta
```

2. **Construir y ejecutar todos los servicios**:
```bash
docker-compose up --build
```

3. **Verificar que todos los servicios estén ejecutándose**:
```bash
docker-compose ps
```

### Ejecución Local (Desarrollo)

1. **Iniciar RabbitMQ**:
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin rabbitmq:3-management
```

2. **Ejecutar servicios en orden**:
```bash
# 1. Eureka Server
cd eureka && mvn spring-boot:run

# 2. API Gateway
cd gateway && mvn spring-boot:run

# 3. Sensor Service
cd sensor && mvn spring-boot:run

# 4. Analyzer Service
cd analyzer && mvn spring-boot:run

# 5. Notification Service
cd notification && mvn spring-boot:run
```

## Rutas del API Gateway

Todos los endpoints están disponibles a través del API Gateway con el prefijo `/api/conjunta/2p/`:

- **Sensor Service**: `http://localhost:8080/api/conjunta/2p/sensor-readings/`
- **Analyzer Service**: `http://localhost:8080/api/conjunta/2p/alerts/`
- **Notification Service**: `http://localhost:8080/api/conjunta/2p/notifications/`

## Ejemplos de Uso

### 1. Enviar Lectura de Sensor

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

### 2. Obtener Alertas Críticas

```bash
curl http://localhost:8080/api/conjunta/2p/alerts/critical
```

### 3. Verificar Notificaciones

```bash
curl http://localhost:8080/api/conjunta/2p/notifications/status/SENT
```

## Monitoreo y Gestión

### Dashboards Disponibles

- **Eureka Dashboard**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672 (admin/admin)
- **CockroachDB UI**: http://localhost:8080 (Node 1)

### Health Checks

- **API Gateway**: http://localhost:8080/actuator/health
- **Sensor Service**: http://localhost:8081/actuator/health
- **Analyzer Service**: http://localhost:8082/actuator/health
- **Notification Service**: http://localhost:8083/actuator/health

## Eventos Globales Implementados

### Eventos Emitidos

1. **NewSensorReadingEvent** (Sensor → Analyzer)
   ```json
   {
     "eventId": "EVT-12345678",
     "sensorId": "S001",
     "type": "temperature",
     "value": 45.0,
     "timestamp": "2024-04-05T12:00:00Z"
   }
   ```

2. **AlertEvent** (Analyzer → Notification)
   ```json
   {
     "alertId": "ALT-12345678",
     "type": "HighTemperatureAlert",
     "sensorId": "S001",
     "value": 45.0,
     "threshold": 40.0,
     "timestamp": "2024-04-05T12:00:00Z",
     "severity": "CRITICAL"
   }
   ```

### Colas RabbitMQ

- `sensor.events.queue`: Eventos de nuevas lecturas
- `alert.events.queue`: Eventos de alertas
- `sensor.inactive.queue`: Sensores inactivos
- `daily.report.queue`: Reportes diarios

## Resiliencia y Tolerancia a Fallos

### Implementada en Sensor Service

1. **Almacenamiento Local**: SQLite cuando RabbitMQ está caído
2. **Reintentos Automáticos**: Hasta 3 intentos con retroceso exponencial
3. **Monitoreo de Salud**: Verificación cada 30 segundos
4. **Recuperación Automática**: Procesamiento de eventos pendientes

### Implementada en Analyzer Service

1. **Manejo de Errores**: Try-catch en procesamiento de eventos
2. **Reintentos**: Spring Retry en publicación de eventos
3. **Logs Detallados**: Para debugging y monitoreo

### Implementada en Notification Service

1. **Simulación de Servicios**: Mock services para email/SMS
2. **Persistencia de Estado**: Almacenamiento de notificaciones enviadas
3. **Reintentos**: Control de intentos de envío

## Persistencia Distribuida

### CockroachDB Cluster

- **Nodo 1** (Puerto 26257): Base de datos del Sensor Service
- **Nodo 2** (Puerto 26258): Base de datos del Analyzer Service  
- **Nodo 3** (Puerto 26259): Base de datos del Notification Service

### Esquemas de Base de Datos

#### sensor_db.sensor_readings
- id, sensor_id, type, value, timestamp

#### analyzer_db.alerts
- id, alert_id, type, sensor_id, value, threshold, timestamp, severity

#### notification_db.notifications
- id, notification_id, event_type, recipient, notification_type, status, priority, message, timestamp

## Configuración por Perfiles

### Perfil Local (H2 Database)
```yaml
spring:
  profiles:
    active: local
  datasource:
    url: jdbc:h2:mem:testdb
```

### Perfil Docker (CockroachDB)
```yaml
spring:
  profiles:
    active: docker
  datasource:
    url: jdbc:postgresql://crdb-node1:26257/sensor_db?sslmode=disable
```

## Logging y Métricas

- **Logs Estructurados**: SLF4J con patrones configurables
- **Niveles por Paquete**: DEBUG para desarrollo, INFO para producción
- **Métricas**: Actuator endpoints habilitados
- **Monitoreo**: Health checks y métricas de rendimiento

## Seguridad

- **Validación de Entrada**: Bean Validation en DTOs
- **Sanitización**: Control de rangos de valores
- **Configuración**: Variables de entorno para credenciales

## Escalabilidad

- **Diseño Stateless**: Todos los servicios son stateless
- **Índices Optimizados**: En consultas frecuentes
- **Procesamiento Asíncrono**: Eventos via RabbitMQ
- **Connection Pooling**: Configurado para base de datos y RabbitMQ

## Comandos Útiles

### Docker
```bash
# Ver logs de un servicio específico
docker-compose logs -f sensor-service

# Reiniciar un servicio
docker-compose restart analyzer-service

# Escalar un servicio
docker-compose up --scale sensor-service=2
```

### Maven
```bash
# Compilar todos los proyectos
mvn clean compile -f sensor/pom.xml
mvn clean compile -f analyzer/pom.xml
mvn clean compile -f notification/pom.xml

# Ejecutar tests
mvn test -f sensor/pom.xml
```

### Base de Datos
```bash
# Conectar a CockroachDB
docker exec -it crdb-node1 ./cockroach sql --insecure

# Ver estado del cluster
docker exec -it crdb-node1 ./cockroach node status --insecure
```

## Arquitectura de Red

```
Internet → API Gateway (8080) → Eureka Discovery (8761)
                ↓
        Load Balancing & Routing
                ↓
    ┌─────────────────┬─────────────────┬─────────────────┐
    │   Sensor (8081) │ Analyzer (8082) │ Notification    │
    │                 │                 │ (8083)          │
    └─────────────────┴─────────────────┴─────────────────┘
                ↓                ↓                ↓
        ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
        │ CockroachDB │  │ CockroachDB │  │ CockroachDB │
        │ Node 1      │  │ Node 2      │  │ Node 3      │
        └─────────────┘  └─────────────┘  └─────────────┘
                        ↑
                 RabbitMQ (5672)
                 Management (15672)
```

Este proyecto implementa completamente todos los requisitos especificados, incluyendo API Gateway, Eureka Server, comunicación asíncrona, persistencia distribuida, y manejo de eventos globales entre microservicios.
