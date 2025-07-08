package  com.eval.conjunta.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
    
    @Value("${app.notification.push.enabled:true}")
    private boolean pushEnabled;
    
    public boolean sendPushNotification(String deviceId, String message) {
        if (!pushEnabled) {
            logger.info("Push notifications deshabilitadas, simulando envío a: {}", deviceId);
            return true;
        }
        
        try {
            logger.info("Enviando push notification a dispositivo: {}", deviceId);
            
            // Simular envío de push notification
            System.out.println("=== PUSH NOTIFICATION ===");
            System.out.println("Dispositivo: " + deviceId);
            System.out.println("Mensaje: " + message);
            System.out.println("Tiempo: " + java.time.Instant.now());
            System.out.println("========================");
            
            logger.info("Push notification simulada enviada exitosamente a: {}", deviceId);
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error al enviar push notification a {}: {}", deviceId, e.getMessage(), e);
            return false;
        }
    }
}
