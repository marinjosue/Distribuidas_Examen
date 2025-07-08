package com.eval.conjunta.notification.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "notification_id", nullable = false, unique = true)
    private String notificationId;
    
    @Column(name = "event_type", nullable = false)
    private String eventType;
    
    @Column(name = "recipient", nullable = false)
    private String recipient;
    
    @Column(name = "notification_type", nullable = false)
    private String notificationType; // EMAIL, SMS, PUSH
    
    @Column(name = "status", nullable = false)
    private String status; // PENDING, SENT, FAILED
    
    @Column(name = "priority", nullable = false)
    private String priority; // CRITICAL, WARNING, INFO
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
    
    @Column(name = "sent_at")
    private Instant sentAt;
    
    @Column(name = "attempts")
    private Integer attempts;
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    // Constructors
    public Notification() {
        this.createdAt = Instant.now();
        this.attempts = 0;
        this.status = "PENDING";
    }
    
    public Notification(String notificationId, String eventType, String recipient, 
                       String notificationType, String priority, String message, Instant timestamp) {
        this();
        this.notificationId = notificationId;
        this.eventType = eventType;
        this.recipient = recipient;
        this.notificationType = notificationType;
        this.priority = priority;
        this.message = message;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public String getNotificationType() {
        return notificationType;
    }
    
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public Instant getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }
    
    public Integer getAttempts() {
        return attempts;
    }
    
    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", notificationId='" + notificationId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", recipient='" + recipient + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
