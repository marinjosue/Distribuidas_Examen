package 
com.eval.conjunta.analyzer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

public class NewSensorReadingEvent {
    
    private String eventId;
    private String sensorId;
    private String type;
    private Double value;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant timestamp;
    
    // Constructors
    public NewSensorReadingEvent() {}
    
    public NewSensorReadingEvent(String eventId, String sensorId, String type, Double value, Instant timestamp) {
        this.eventId = eventId;
        this.sensorId = sensorId;
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    public String getSensorId() {
        return sensorId;
    }
    
    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Double getValue() {
        return value;
    }
    
    public void setValue(Double value) {
        this.value = value;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "NewSensorReadingEvent{" +
                "eventId='" + eventId + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", type='" + type + '\'' +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}
