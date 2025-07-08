package com.eval.conjunta.analyzer.service;

import com.eval.conjunta.analyzer.model.Alert;
import com.eval.conjunta.analyzer.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class AlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    
    @Autowired
    private AlertRepository alertRepository;
    
    public List<Alert> getAllAlerts() {
        logger.info("Obteniendo todas las alertas");
        return alertRepository.findAll();
    }
    
    public List<Alert> getAlertsBySensor(String sensorId) {
        logger.info("Obteniendo alertas para el sensor: {}", sensorId);
        return alertRepository.findBySensorIdOrderByTimestampDesc(sensorId);
    }
    
    public List<Alert> getAlertsByType(String type) {
        logger.info("Obteniendo alertas por tipo: {}", type);
        return alertRepository.findByTypeOrderByTimestampDesc(type);
    }
    
    public List<Alert> getAlertsBySeverity(String severity) {
        logger.info("Obteniendo alertas por severidad: {}", severity);
        return alertRepository.findBySeverityOrderByTimestampDesc(severity);
    }
    
    public List<Alert> getCriticalAlerts() {
        logger.info("Obteniendo alertas críticas de las últimas 24 horas");
        Instant oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS);
        return alertRepository.findCriticalAlertsAfter(oneDayAgo);
    }
    
    public List<Alert> getAlertsInTimeRange(Instant startTime, Instant endTime) {
        logger.info("Obteniendo alertas entre {} y {}", startTime, endTime);
        return alertRepository.findByTimestampBetween(startTime, endTime);
    }
    
    public long countAlertsBySensor(String sensorId, Instant startTime) {
        logger.info("Contando alertas para el sensor {} desde {}", sensorId, startTime);
        return alertRepository.countBySensorIdAndTimestampAfter(sensorId, startTime);
    }
}
