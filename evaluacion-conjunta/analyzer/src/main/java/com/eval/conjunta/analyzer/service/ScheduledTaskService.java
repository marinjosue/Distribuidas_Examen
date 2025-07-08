package com.eval.conjunta.analyzer.service;

import com.eval.conjunta.analyzer.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
public class ScheduledTaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);
    
    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private EventPublisherService eventPublisherService;
    
    /**
     * Genera reportes diarios cada día a medianoche
     */
    @Scheduled(cron = "${app.analyzer.scheduling.daily-report:0 0 0 * * *}")
    public void generateDailyReport() {
        logger.info("Iniciando generación de reporte diario");
        
        try {
            Instant yesterday = LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC);
            Instant today = LocalDateTime.now().toInstant(ZoneOffset.UTC);
            
            // Obtener alertas del último día
            var alerts = alertRepository.findByTimestampBetween(yesterday, today);
            
            // Calcular estadísticas
            Map<String, Object> report = new HashMap<>();
            report.put("date", today.toString());
            report.put("totalAlerts", alerts.size());
            report.put("criticalAlerts", alerts.stream()
                    .filter(alert -> "CRITICAL".equals(alert.getSeverity()))
                    .count());
            report.put("warningAlerts", alerts.stream()
                    .filter(alert -> "WARNING".equals(alert.getSeverity()))
                    .count());
            
            // Estadísticas por tipo de alerta
            Map<String, Long> alertsByType = new HashMap<>();
            alerts.forEach(alert -> 
                alertsByType.merge(alert.getType(), 1L, Long::sum)
            );
            report.put("alertsByType", alertsByType);
            
            // Publicar evento de reporte diario
            eventPublisherService.publishDailyReportEvent(report);
            
            logger.info("Reporte diario generado exitosamente. Total alertas: {}", alerts.size());
            
        } catch (Exception e) {
            logger.error("Error al generar reporte diario: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Verifica sensores inactivos cada 6 horas
     */
    @Scheduled(cron = "${app.analyzer.scheduling.sensor-check:0 0 */6 * * *}")
    public void checkInactiveSensors() {
        logger.info("Iniciando verificación de sensores inactivos");
        
        try {
            Instant twentyFourHoursAgo = Instant.now().minusSeconds(24 * 60 * 60);
            
            // Obtener sensores únicos de las alertas recientes
            var recentAlerts = alertRepository.findByTimestampBetween(twentyFourHoursAgo, Instant.now());
            
            // Identificar sensores que no han enviado lecturas en 24 horas
            // En un escenario real, esto se haría consultando directamente las lecturas de sensores
            // o manteniendo una tabla de último heartbeat de sensores
            
            Map<String, Instant> lastSensorActivity = new HashMap<>();
            recentAlerts.forEach(alert -> {
                String sensorId = alert.getSensorId();
                Instant timestamp = alert.getTimestamp();
                lastSensorActivity.merge(sensorId, timestamp, 
                    (existing, newTime) -> existing.isAfter(newTime) ? existing : newTime);
            });
            
            // Verificar sensores inactivos
            lastSensorActivity.forEach((sensorId, lastActivity) -> {
                if (lastActivity.isBefore(twentyFourHoursAgo)) {
                    logger.warn("Sensor inactivo detectado: {} - Última actividad: {}", sensorId, lastActivity);
                    eventPublisherService.publishSensorInactiveEvent(sensorId);
                }
            });
            
            logger.info("Verificación de sensores inactivos completada");
            
        } catch (Exception e) {
            logger.error("Error al verificar sensores inactivos: {}", e.getMessage(), e);
        }
    }
}
