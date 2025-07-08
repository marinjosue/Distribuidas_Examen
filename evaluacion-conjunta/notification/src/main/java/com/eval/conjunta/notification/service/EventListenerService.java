package  com.eval.conjunta.notification.service;

import com.eval.conjunta.notification.dto.AlertEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventListenerService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventListenerService.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @RabbitListener(queues = "#{alertEventQueue.name}")
    public void handleAlertEvent(AlertEvent alertEvent) {
        logger.info("Recibido evento de alerta: {} - Tipo: {} - Severidad: {}", 
                alertEvent.getAlertId(), alertEvent.getType(), alertEvent.getSeverity());
        
        try {
            notificationService.processAlertEvent(alertEvent);
        } catch (Exception e) {
            logger.error("Error al procesar evento de alerta: {}", e.getMessage(), e);
        }
    }
    
    @RabbitListener(queues = "sensor.inactive.queue")
    public void handleSensorInactiveEvent(String sensorId) {
        logger.info("Recibido evento de sensor inactivo: {}", sensorId);
        
        try {
            // Crear alerta ficticia para sensor inactivo
            AlertEvent inactiveEvent = new AlertEvent();
            inactiveEvent.setAlertId("INACTIVE-" + sensorId);
            inactiveEvent.setType("SensorInactiveAlert");
            inactiveEvent.setSensorId(sensorId);
            inactiveEvent.setValue(0.0);
            inactiveEvent.setThreshold(0.0);
            inactiveEvent.setTimestamp(java.time.Instant.now());
            inactiveEvent.setSeverity("WARNING");
            
            notificationService.processAlertEvent(inactiveEvent);
        } catch (Exception e) {
            logger.error("Error al procesar evento de sensor inactivo: {}", e.getMessage(), e);
        }
    }
    
    @RabbitListener(queues = "daily.report.queue")
    public void handleDailyReportEvent(Object reportData) {
        logger.info("Recibido evento de reporte diario");
        
        try {
            // Crear alerta ficticia para reporte diario
            AlertEvent reportEvent = new AlertEvent();
            reportEvent.setAlertId("REPORT-" + java.time.Instant.now().getEpochSecond());
            reportEvent.setType("DailyReportGenerated");
            reportEvent.setSensorId("SYSTEM");
            reportEvent.setValue(0.0);
            reportEvent.setThreshold(0.0);
            reportEvent.setTimestamp(java.time.Instant.now());
            reportEvent.setSeverity("INFO");
            
            notificationService.processAlertEvent(reportEvent);
        } catch (Exception e) {
            logger.error("Error al procesar evento de reporte diario: {}", e.getMessage(), e);
        }
    }
}
