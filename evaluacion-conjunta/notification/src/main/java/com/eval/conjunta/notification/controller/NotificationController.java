package  com.eval.conjunta.notification.controller;

import com.eval.conjunta.notification.model.Notification;
import com.eval.conjunta.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        logger.info("Obteniendo todas las notificaciones");
        try {
            List<Notification> notifications = notificationService.getAllNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Error al obtener notificaciones: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Notification>> getNotificationsByStatus(@PathVariable String status) {
        logger.info("Obteniendo notificaciones por estado: {}", status);
        try {
            List<Notification> notifications = notificationService.getNotificationsByStatus(status);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Error al obtener notificaciones por estado {}: {}", status, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByType(@PathVariable String type) {
        logger.info("Obteniendo notificaciones por tipo: {}", type);
        try {
            List<Notification> notifications = notificationService.getNotificationsByType(type);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            logger.error("Error al obtener notificaciones por tipo {}: {}", type, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/mock-email")
    public ResponseEntity<Map<String, String>> mockEmailEndpoint(@RequestBody Map<String, Object> emailData) {
        logger.info("Endpoint mock de email recibido: {}", emailData);
        
        // Simular procesamiento de email
        Map<String, String> response = Map.of(
            "status", "success",
            "message", "Email mock procesado exitosamente",
            "recipient", emailData.getOrDefault("recipient", "unknown").toString()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/mock-sms")
    public ResponseEntity<Map<String, String>> mockSmsEndpoint(@RequestBody Map<String, Object> smsData) {
        logger.info("Endpoint mock de SMS recibido: {}", smsData);
        
        // Simular procesamiento de SMS
        Map<String, String> response = Map.of(
            "status", "success",
            "message", "SMS mock procesado exitosamente",
            "phoneNumber", smsData.getOrDefault("phoneNumber", "unknown").toString()
        );
        
        return ResponseEntity.ok(response);
    }
}
