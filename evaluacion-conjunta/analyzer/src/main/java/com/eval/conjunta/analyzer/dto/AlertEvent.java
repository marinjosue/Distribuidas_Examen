
package com.eval.conjunta.analyzer.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

public class AlertEvent {
    
    private String alertId;
    private String type;
    private String sensorId;
    private Double value;
    private Double threshold;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant timestamp;
    
    private String severity;
    
    // Constructors
    public AlertEvent() {}
    
    public AlertEvent(String alertId, String type, String sensorId, Double value, Double threshold, Instant timestamp, String severity) {
        this.alertId = alertId;
        this.type = type;
        this.sensorId = sensorId;
        this.value = value;
        this.threshold = threshold;
        this.timestamp = timestamp;
        this.severity = severity;
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "AlertEvent{" +
                "alertId='" + alertId + '\'' +
                ", type='" + type + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", value=" + value +
                ", threshold=" + threshold +
                ", timestamp=" + timestamp +
                ", severity='" + severity + '\'' +
                '}';
    }
}
