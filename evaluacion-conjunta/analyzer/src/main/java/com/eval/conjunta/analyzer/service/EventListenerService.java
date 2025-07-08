package com.eval.conjunta.analyzer.service;

import com.eval.conjunta.analyzer.dto.NewSensorReadingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventListenerService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventListenerService.class);
    
    @Autowired
    private AnalysisService analysisService;
    
    @RabbitListener(queues = "#{sensorEventQueue.name}")
    public void handleNewSensorReadingEvent(NewSensorReadingEvent event) {
        logger.info("Recibido evento NewSensorReadingEvent: {} de sensor: {}", 
                event.getEventId(), event.getSensorId());
        
        try {
            analysisService.processSensorReading(event);
        } catch (Exception e) {
            logger.error("Error al procesar evento de sensor: {}", e.getMessage(), e);
        }
    }
}
