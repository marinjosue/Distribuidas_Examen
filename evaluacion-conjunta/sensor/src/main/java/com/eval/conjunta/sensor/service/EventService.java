package com.eval.conjunta.sensor.service;

import com.eval.conjunta.sensor.dto.NewSensorReadingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private static final String EXCHANGE = "events.exchange";
    private static final String ROUTING_KEY = "sensor.new";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    public EventService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Retryable(value = {AmqpException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void publishNewSensorReadingEvent(NewSensorReadingEvent event) {
        try {
            logger.info("Publicando evento NewSensorReadingEvent: {}", event.getEventId());
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
            logger.info("Evento publicado exitosamente");
        } catch (Exception e) {
            logger.error("Error al publicar evento a RabbitMQ: {}", e.getMessage());
            // Ya no se almacena localmente el evento
        }
    }
}
           