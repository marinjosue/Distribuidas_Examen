-- Script para inicializar la base de datos CockroachDB para el microservicio de sensores

-- Crear la base de datos (ejecutar como administrador)
CREATE DATABASE IF NOT EXISTS sensor_db;

-- Usar la base de datos
USE sensor_db;

-- Crear la tabla sensor_readings
CREATE TABLE IF NOT EXISTS sensor_readings (
    id SERIAL PRIMARY KEY,
    sensor_id VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    INDEX idx_sensor_id (sensor_id),
    INDEX idx_type (type),
    INDEX idx_timestamp (timestamp),
    INDEX idx_sensor_timestamp (sensor_id, timestamp DESC)
);

-- Crear tabla para eventos pendientes (si se necesita para procesamiento asíncrono)
CREATE TABLE IF NOT EXISTS pending_events (
    id SERIAL PRIMARY KEY,
    event_data TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    INDEX idx_created_at (created_at)
);

-- Insertar datos de ejemplo
INSERT INTO sensor_readings (sensor_id, type, value, timestamp) VALUES
    ('TEMP001', 'temperature', 23.5, '2025-01-01 10:00:00+00:00'),
    ('TEMP001', 'temperature', 24.1, '2025-01-01 10:30:00+00:00'),
    ('TEMP001', 'temperature', 22.8, '2025-01-01 11:00:00+00:00'),
    ('HUM001', 'humidity', 65.0, '2025-01-01 10:00:00+00:00'),
    ('HUM001', 'humidity', 67.2, '2025-01-01 10:30:00+00:00'),
    ('PRESS001', 'pressure', 1013.25, '2025-01-01 10:00:00+00:00'),
    ('PRESS001', 'pressure', 1012.8, '2025-01-01 10:30:00+00:00')
ON CONFLICT DO NOTHING;

-- Verificar los datos insertados
SELECT * FROM sensor_readings ORDER BY timestamp DESC LIMIT 10;
