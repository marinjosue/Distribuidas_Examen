package  com.eval.conjunta.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Value("${app.notification.email.enabled:true}")
    private boolean emailEnabled;
    
    @Value("${app.notification.email.mock-service-url:http://localhost:8083/mock-email}")
    private String mockServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public EmailService() {
        this.restTemplate = new RestTemplate();
    }
    
    public boolean sendEmail(String recipient, String subject, String message) {
        if (!emailEnabled) {
            logger.info("Email deshabilitado, simulando envío a: {}", recipient);
            return true;
        }
        
        try {
            logger.info("Enviando email a: {} - Asunto: {}", recipient, subject);
            
            // Simular envío de email mediante servicio mock
            EmailRequest emailRequest = new EmailRequest(recipient, subject, message);
            
            // En lugar de hacer una llamada HTTP real, simplemente simulamos
            logger.info("Email simulado enviado exitosamente a: {}", recipient);
            logger.info("Contenido del email: {}", message);
            
            return true;
            
        } catch (Exception e) {
            logger.error("Error al enviar email a {}: {}", recipient, e.getMessage(), e);
            return false;
        }
    }
    
    private static class EmailRequest {
        private String recipient;
        private String subject;
        private String message;
        
        public EmailRequest(String recipient, String subject, String message) {
            this.recipient = recipient;
            this.subject = subject;
            this.message = message;
        }
        
        // Getters and setters
        public String getRecipient() { return recipient; }
        public void setRecipient(String recipient) { this.recipient = recipient; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
