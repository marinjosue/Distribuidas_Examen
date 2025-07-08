package com.eval.conjunta.sensor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SensorValidationService {

    private static final Logger logger = LoggerFactory.getLogger(SensorValidationService.class);

    @Value("${app.sensor.validation.temperature.min:-50.0}")
    private double temperatureMin;

    @Value("${app.sensor.validation.temperature.max:60.0}")
    private double temperatureMax;

    @Value("${app.sensor.validation.humidity.min:0.0}")
    private double humidityMin;

    @Value("${app.sensor.validation.humidity.max:100.0}")
    private double humidityMax;

    public void validateSensorReading(String sensorType, double value) {
        logger.debug("Validando lectura de sensor tipo: {} con valor: {}", sensorType, value);
        
        switch (sensorType.toLowerCase()) {
            case "temperature":
                validateTemperature(value);
                break;
            case "humidity":
                validateHumidity(value);
                break;
            case "pressure":
                validatePressure(value);
                break;
            default:
                logger.warn("Tipo de sensor desconocido: {}", sensorType);
                // Para tipos desconocidos, aplicar validación básica
                validateGeneric(value);
        }
    }

    private void validateTemperature(double value) {
        if (value < temperatureMin || value > temperatureMax) {
            throw new IllegalArgumentException(
                String.format("Valor de temperatura fuera de rango: %.2f°C. Rango válido: %.2f°C a %.2f°C",
                    value, temperatureMin, temperatureMax)
            );
        }
    }

    private void validateHumidity(double value) {
        if (value < humidityMin || value > humidityMax) {
            throw new IllegalArgumentException(
                String.format("Valor de humedad fuera de rango: %.2f%%. Rango válido: %.2f%% a %.2f%%",
                    value, humidityMin, humidityMax)
            );
        }
    }

    private void validatePressure(double value) {
        // Presión atmosférica típica: 800-1200 hPa
        if (value < 800.0 || value > 1200.0) {
            throw new IllegalArgumentException(
                String.format("Valor de presión fuera de rango: %.2f hPa. Rango válido: 800.0 hPa a 1200.0 hPa", value)
            );
        }
    }

    private void validateGeneric(double value) {
        // Validación básica para tipos desconocidos
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Valor de sensor inválido: " + value);
        }
        
        // Rango muy amplio para valores desconocidos
        if (value < -1000.0 || value > 1000.0) {
            throw new IllegalArgumentException(
                String.format("Valor de sensor fuera de rango razonable: %.2f", value)
            );
        }
    }
}
