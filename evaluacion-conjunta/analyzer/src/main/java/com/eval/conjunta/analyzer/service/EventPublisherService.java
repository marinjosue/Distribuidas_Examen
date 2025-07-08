package com.eval.conjunta.analyzer.service;

import com.eval.conjunta.analyzer.dto.AlertEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventPublisherService.class);
    private static final String EXCHANGE = "events.exchange";
    private static final String ALERT_ROUTING_KEY = "alert.created";
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    private final ObjectMapper objectMapper;
    
    public EventPublisherService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void publishAlertEvent(AlertEvent alertEvent) {
        try {
            logger.info("Publicando evento de alerta: {} - Tipo: {}", alertEvent.getAlertId(), alertEvent.getType());
            rabbitTemplate.convertAndSend(EXCHANGE, ALERT_ROUTING_KEY, alertEvent);
            logger.info("Evento de alerta publicado exitosamente");
        } catch (Exception e) {
            logger.error("Error al publicar evento de alerta: {}", e.getMessage(), e);
            throw e; // Re-lanzar para activar el retry
        }
    }
    
    public void publishDailyReportEvent(Object reportData) {
        try {
            logger.info("Publicando evento de reporte diario");
            rabbitTemplate.convertAndSend(EXCHANGE, "report.daily", reportData);
            logger.info("Evento de reporte diario publicado exitosamente");
        } catch (Exception e) {
            logger.error("Error al publicar evento de reporte diario: {}", e.getMessage(), e);
        }
    }
    
    public void publishSensorInactiveEvent(String sensorId) {
        try {
            logger.info("Publicando evento de sensor inactivo: {}", sensorId);
            rabbitTemplate.convertAndSend(EXCHANGE, "sensor.inactive", sensorId);
            logger.info("Evento de sensor inactivo publicado exitosamente");
        } catch (Exception e) {
            logger.error("Error al publicar evento de sensor inactivo: {}", e.getMessage(), e);
        }
    }
}
