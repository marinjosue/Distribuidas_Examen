# Microservicio de Sensores

Este microservicio se encarga de recopilar, almacenar y gestionar datos de sensores en un sistema distribuido.

## Características

- **Recolección de datos**: Recibe datos de sensores en formato JSON
- **Almacenamiento**: Guarda los datos en CockroachDB
- **Consultas**: Proporciona APIs para consultar el historial de lecturas
- **Integración**: Se integra con Eureka para descubrimiento de servicios

## Endpoints de la API

### POST /sensor-readings
Recibe datos de sensores en formato JSON.

**Cuerpo de la petición:**
```json
{
  "sensor_id": "TEMP001",
  "type": "temperature",
  "value": 23.5,
  "timestamp": "2025-01-01T10:00:00Z"
}
```

**Respuesta exitosa (201 Created):**
```json
{
  "id": 1,
  "sensorId": "TEMP001",
  "type": "temperature",
  "value": 23.5,
  "timestamp": "2025-01-01T10:00:00Z"
}
```

### GET /sensor-readings/{sensorId}
Devuelve el historial de lecturas de un sensor específico.

**Parámetros:**
- `sensorId`: ID del sensor (requerido)

**Respuesta exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "sensorId": "TEMP001",
    "type": "temperature",
    "value": 23.5,
    "timestamp": "2025-01-01T10:00:00Z"
  },
  {
    "id": 2,
    "sensorId": "TEMP001",
    "type": "temperature",
    "value": 24.1,
    "timestamp": "2025-01-01T10:30:00Z"
  }
]
```

### Endpoints adicionales

- `GET /sensor-readings` - Obtiene todas las lecturas
- `GET /sensor-readings/{sensorId}/latest` - Obtiene la lectura más reciente
- `GET /sensor-readings/{sensorId}/count` - Cuenta las lecturas de un sensor
- `GET /sensor-readings/{sensorId}/range` - Obtiene lecturas en un rango de tiempo
- `GET /sensor-readings/type/{type}` - Obtiene lecturas por tipo de sensor
- `DELETE /sensor-readings/{id}` - Elimina una lectura específica

## Configuración de Base de Datos

El microservicio utiliza CockroachDB con la siguiente configuración:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:26257/sensor_db?sslmode=disable
    username: root
    password: 
    driver-class-name: org.postgresql.Driver
```

### Tabla sensor_readings

La tabla principal tiene la siguiente estructura:

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | SERIAL | ID único (clave primaria) |
| sensor_id | VARCHAR(255) | ID del sensor |
| type | VARCHAR(255) | Tipo de sensor |
| value | DOUBLE PRECISION | Valor de la lectura |
| timestamp | TIMESTAMP WITH TIME ZONE | Momento de la lectura |
  "timestamp": "2024-04-05T12:00:00Z"
}
```

## Configuración

### Variables de Entorno
- `SPRING_DATASOURCE_URL`: URL de conexión a CockroachDB
- `SPRING_DATASOURCE_USERNAME`: Usuario de la base de datos
- `SPRING_DATASOURCE_PASSWORD`: Contraseña de la base de datos
- `SPRING_RABBITMQ_HOST`: Host de RabbitMQ
- `SPRING_RABBITMQ_PORT`: Puerto de RabbitMQ
- `SPRING_RABBITMQ_USERNAME`: Usuario de RabbitMQ
- `SPRING_RABBITMQ_PASSWORD`: Contraseña de RabbitMQ

### Configuración de Validación
```yaml
app:
  sensor:
    validation:
      temperature:
        min: -50.0
        max: 60.0
      humidity:
        min: 0.0
        max: 100.0
```

## Tecnologías Utilizadas
- **Spring Boot 3.5.3**
- **Spring Data JPA**
- **Spring AMQP (RabbitMQ)**
- **Spring Validation**
- **PostgreSQL/CockroachDB**
- **SQLite** (para almacenamiento local de eventos)
- **Jackson** (serialización JSON)
- **Spring Retry** (reintentos automáticos)

## Compilación y Ejecución

### Prerrequisitos
- Java 17+
- Maven 3.6+
- CockroachDB cluster
- RabbitMQ server

### Compilar
```bash
mvn clean compile
```

### Ejecutar Tests
```bash
mvn test
```

### Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

### Crear JAR
```bash
mvn clean package
```

## Endpoints de Monitoreo
- `GET /actuator/health`: Estado de salud del servicio
- `GET /actuator/info`: Información del servicio
- `GET /actuator/metrics`: Métricas de la aplicación

## Ejemplos de Uso

### Enviar Lectura de Sensor
```bash
curl -X POST http://localhost:8081/sensor-readings \
  -H "Content-Type: application/json" \
  -d '{
    "sensorId": "S001",
    "type": "temperature",
    "value": 25.5,
    "timestamp": "2024-04-05T12:00:00Z"
  }'
```

### Obtener Historial de Sensor
```bash
curl http://localhost:8081/sensor-readings/S001
```

### Obtener Todas las Lecturas
```bash
curl http://localhost:8081/sensor-readings
```

## Arquitectura de Resiliencia

1. **Almacenamiento Primario**: CockroachDB para persistencia principal
2. **Almacenamiento Local**: SQLite para eventos cuando RabbitMQ está caído
3. **Reintento Automático**: Spring Retry con retroceso exponencial
4. **Monitoreo de Salud**: Verificación periódica de conectividad
5. **Recuperación Automática**: Procesamiento de eventos pendientes

## Logging
- Logs estructurados con SLF4J
- Nivel de log configurable por paquete
- Métricas de rendimiento incluidas

## Seguridad
- Validación estricta de datos de entrada
- Sanitización de parámetros
- Control de rangos de valores por tipo de sensor

## Escalabilidad
- Diseño stateless para escalado horizontal
- Índices optimizados en base de datos
- Procesamiento asíncrono de eventos
- Conexiones pooled para RabbitMQ y base de datos
