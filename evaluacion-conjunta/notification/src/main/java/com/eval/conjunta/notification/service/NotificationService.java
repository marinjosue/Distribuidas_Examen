package com.eval.conjunta.notification.service;

import com.eval.conjunta.notification.dto.AlertEvent;
import com.eval.conjunta.notification.model.Notification;
import com.eval.conjunta.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private PushNotificationService pushService;

    @Value("${app.notification.priority.critical-immediate:true}")
    private boolean criticalImmediate;

    @Value("${app.notification.priority.warning-batch:true}")
    private boolean warningBatch;

    @Value("${app.notification.priority.info-batch:true}")
    private boolean infoBatch;

    public void processAlertEvent(AlertEvent alertEvent) {
        logger.info("Procesando evento de alerta: {} - Tipo: {} - Severidad: {}",
                alertEvent.getAlertId(), alertEvent.getType(), alertEvent.getSeverity());

        try {
            String priority = determinePriority(alertEvent.getSeverity());
            String message = generateMessage(alertEvent);

            // Crear notificaciones para diferentes canales
            createNotifications(alertEvent, priority, message);

            // Enviar inmediatamente si es crítico
            if ("CRITICAL".equals(priority) && criticalImmediate) {
                sendCriticalNotifications(alertEvent.getAlertId());
            }

        } catch (Exception e) {
            logger.error("Error al procesar evento de alerta: {}", e.getMessage(), e);
        }
    }

    private String determinePriority(String severity) {
        switch (severity.toUpperCase()) {
            case "CRITICAL":
                return "CRITICAL";
            case "WARNING":
                return "WARNING";
            default:
                return "INFO";
        }
    }

    private String generateMessage(AlertEvent alertEvent) {
        return String.format(
                "Alerta %s detectada en sensor %s. Valor: %.2f, Umbral: %.2f. Tiempo: %s",
                alertEvent.getType(),
                alertEvent.getSensorId(),
                alertEvent.getValue(),
                alertEvent.getThreshold(),
                alertEvent.getTimestamp());
    }

    private void createNotifications(AlertEvent alertEvent, String priority, String message) {
        String notificationId = "NOT-" + UUID.randomUUID().toString().substring(0, 8);

        // Crear notificación por email
        Notification emailNotification = new Notification(
                notificationId + "-EMAIL",
                alertEvent.getType(),
                getRecipientForEventType(alertEvent.getType(), "EMAIL"),
                "EMAIL",
                priority,
                message,
                alertEvent.getTimestamp());
        notificationRepository.save(emailNotification);

        // Crear notificación por SMS para alertas críticas
        if ("CRITICAL".equals(priority)) {
            Notification smsNotification = new Notification(
                    notificationId + "-SMS",
                    alertEvent.getType(),
                    getRecipientForEventType(alertEvent.getType(), "SMS"),
                    "SMS",
                    priority,
                    message,
                    alertEvent.getTimestamp());
            notificationRepository.save(smsNotification);
        }

        // Crear notificación push
        Notification pushNotification = new Notification(
                notificationId + "-PUSH",
                alertEvent.getType(),
                getRecipientForEventType(alertEvent.getType(), "PUSH"),
                "PUSH",
                priority,
                message,
                alertEvent.getTimestamp());
        notificationRepository.save(pushNotification);

        logger.info("Notificaciones creadas para alerta: {}", alertEvent.getAlertId());
    }

    private String getRecipientForEventType(String eventType, String notificationType) {
        // En un sistema real, esto sería configurado por tipo de evento
        switch (notificationType) {
            case "EMAIL":
                return "admin@example.com";
            case "SMS":
                return "+1234567890";
            case "PUSH":
                return "admin-device";
            default:
                return "default-recipient";
        }
    }

    private void sendCriticalNotifications(String alertId) {
        logger.info("Enviando notificaciones críticas para alerta: {}", alertId);

        List<Notification> criticalNotifications = notificationRepository
                .findByStatusAndPriority("PENDING", "CRITICAL");

        for (Notification notification : criticalNotifications) {
            sendNotification(notification);
        }
    }

    @Scheduled(fixedDelayString = "${app.notification.scheduling.batch-notifications:1800000}") // 30 minutos
    public void processBatchNotifications() {
        logger.info("Procesando notificaciones en lote");

        try {
            // Procesar notificaciones pendientes de baja prioridad
            List<Notification> pendingNotifications = notificationRepository
                    .findByStatusAndPriorityIn("PENDING", List.of("WARNING", "INFO"));

            for (Notification notification : pendingNotifications) {
                sendNotification(notification);
            }

            logger.info("Procesadas {} notificaciones en lote", pendingNotifications.size());

        } catch (Exception e) {
            logger.error("Error al procesar notificaciones en lote: {}", e.getMessage(), e);
        }
    }

    private void sendNotification(Notification notification) {
        try {
            notification.setAttempts(notification.getAttempts() + 1);

            boolean sent = false;
            switch (notification.getNotificationType()) {
                case "EMAIL":
                    sent = emailService.sendEmail(notification.getRecipient(),
                            "Alerta: " + notification.getEventType(),
                            notification.getMessage());
                    break;
                case "SMS":
                    sent = smsService.sendSms(notification.getRecipient(),
                            notification.getMessage());
                    break;
                case "PUSH":
                    sent = pushService.sendPushNotification(notification.getRecipient(),
                            notification.getMessage());
                    break;
            }

            if (sent) {
                notification.setStatus("SENT");
                notification.setSentAt(Instant.now());
                logger.info("Notificación enviada: {} - Tipo: {}",
                        notification.getNotificationId(), notification.getNotificationType());
            } else {
                notification.setStatus("FAILED");
                logger.warn("Fallo al enviar notificación: {} - Intento: {}",
                        notification.getNotificationId(), notification.getAttempts());
            }

            notificationRepository.save(notification);

        } catch (Exception e) {
            logger.error("Error al enviar notificación {}: {}",
                    notification.getNotificationId(), e.getMessage(), e);
            notification.setStatus("FAILED");
            notificationRepository.save(notification);
        }
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByStatus(String status) {
        return notificationRepository.findByStatus(status);
    }

    public List<Notification> getNotificationsByType(String type) {
        return notificationRepository.findByNotificationType(type);
    }
}
