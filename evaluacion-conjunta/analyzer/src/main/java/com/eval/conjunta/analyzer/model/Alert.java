package com.eval.conjunta.analyzer.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "alerts")
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "alert_id", nullable = false, unique = true)
    private String alertId;
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @Column(name = "sensor_id", nullable = false)
    private String sensorId;
    
    @Column(name = "value", nullable = false)
    private Double value;
    
    @Column(name = "threshold", nullable = false)
    private Double threshold;
    
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
    
    @Column(name = "severity")
    private String severity;
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    // Constructors
    public Alert() {
        this.createdAt = Instant.now();
    }
    
    public Alert(String alertId, String type, String sensorId, Double value, Double threshold, Instant timestamp, String severity) {
        this();
        this.alertId = alertId;
        this.type = type;
        this.sensorId = sensorId;
        this.value = value;
        this.threshold = threshold;
        this.timestamp = timestamp;
        this.severity = severity;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAlertId() {
        return alertId;
    }
    
    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getSensorId() {
        return sensorId;
    }
    
    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }
    
    public Double getValue() {
        return value;
    }
    
    public void setValue(Double value) {
        this.value = value;
    }
    
    public Double getThreshold() {
        return threshold;
    }
    
    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", alertId='" + alertId + '\'' +
                ", type='" + type + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", value=" + value +
                ", threshold=" + threshold +
                ", timestamp=" + timestamp +
                ", severity='" + severity + '\'' +
                '}';
    }
}
