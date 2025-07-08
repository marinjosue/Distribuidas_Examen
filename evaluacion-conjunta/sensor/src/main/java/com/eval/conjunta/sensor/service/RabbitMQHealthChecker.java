package com.eval.conjunta.sensor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQHealthChecker {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQHealthChecker.class);

    @Autowired
    private ConnectionFactory connectionFactory;

    private boolean previouslyConnected = false;

    @Scheduled(fixedDelay = 30000) // Verificar cada 30 segundos
    public void checkRabbitMQHealth() {
        try {
            // Intentar crear una conexión
            var connection = connectionFactory.createConnection();
            if (connection.isOpen()) {
                connection.close();

                // Si anteriormente estaba desconectado y ahora está conectado
                if (!previouslyConnected) {
                    logger.info("RabbitMQ está disponible");
                    previouslyConnected = true;
                }
            }
        } catch (Exception e) {
            if (previouslyConnected) {
                logger.warn("RabbitMQ no está disponible: {}", e.getMessage());
                previouslyConnected = false;
            }
        }
    }
}
  