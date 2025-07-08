package com.eval.conjunta.analyzer.service;

import com.eval.conjunta.analyzer.dto.NewSensorReadingEvent;
import com.eval.conjunta.analyzer.dto.AlertEvent;
import com.eval.conjunta.analyzer.model.Alert;
import com.eval.conjunta.analyzer.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class AnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private EventPublisherService eventPublisherService;
    
    @Value("${app.analyzer.thresholds.temperature.high:40.0}")
    private Double temperatureHighThreshold;
    
    @Value("${app.analyzer.thresholds.humidity.low:20.0}")
    private Double humidityLowThreshold;
    
    @Value("${app.analyzer.thresholds.seismic.critical:3.0}")
    private Double seismicCriticalThreshold;
    
    public void processSensorReading(NewSensorReadingEvent event) {
        logger.info("Procesando lectura del sensor: {} - {}", event.getSensorId(), event.getType());
        
        try {
            switch (event.getType().toLowerCase()) {
                case "temperature":
                    analyzeTemperature(event);
                    break;
                case "humidity":
                    analyzeHumidity(event);
                    break;
                case "seismic":
                    analyzeSeismic(event);
                    break;
                default:
                    logger.debug("Tipo de sensor no reconocido para análisis: {}", event.getType());
            }
        } catch (Exception e) {
            logger.error("Error al procesar lectura del sensor: {}", e.getMessage(), e);
        }
    }
    
    private void analyzeTemperature(NewSensorReadingEvent event) {
        if (event.getValue() > temperatureHighThreshold) {
            createAlert(
                "HighTemperatureAlert",
                event.getSensorId(),
                event.getValue(),
                temperatureHighThreshold,
                event.getTimestamp(),
                "CRITICAL"
            );
        }
    }
    
    private void analyzeHumidity(NewSensorReadingEvent event) {
        if (event.getValue() < humidityLowThreshold) {
            createAlert(
                "LowHumidityWarning",
                event.getSensorId(),
                event.getValue(),
                humidityLowThreshold,
                event.getTimestamp(),
                "WARNING"
            );
        }
    }
    
    private void analyzeSeismic(NewSensorReadingEvent event) {
        if (event.getValue() > seismicCriticalThreshold) {
            createAlert(
                "SeismicActivityDetected",
                event.getSensorId(),
                event.getValue(),
                seismicCriticalThreshold,
                event.getTimestamp(),
                "CRITICAL"
            );
        }
    }
    
    private void createAlert(String alertType, String sensorId, Double value, Double threshold, Instant timestamp, String severity) {
        try {
            String alertId = "ALT-" + UUID.randomUUID().toString().substring(0, 8);
            
            // Crear y guardar la alerta
            Alert alert = new Alert(alertId, alertType, sensorId, value, threshold, timestamp, severity);
            Alert savedAlert = alertRepository.save(alert);
            
            logger.info("Alerta creada: {} - Sensor: {} - Valor: {} - Umbral: {}", 
                    alertType, sensorId, value, threshold);
            
            // Crear y publicar evento de alerta
            AlertEvent alertEvent = new AlertEvent(
                    alertId, alertType, sensorId, value, threshold, timestamp, severity
            );
            
            eventPublisherService.publishAlertEvent(alertEvent);
            
        } catch (Exception e) {
            logger.error("Error al crear alerta: {}", e.getMessage(), e);
        }
    }
}
