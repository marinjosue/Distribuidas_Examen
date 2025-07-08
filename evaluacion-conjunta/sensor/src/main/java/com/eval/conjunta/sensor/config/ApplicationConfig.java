package com.eval.conjunta.sensor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "false")
    public ConnectionFactory disabledConnectionFactory() {
        logger.warn("RabbitMQ está deshabilitado - usando mock ConnectionFactory");
        return new MockConnectionFactory();
    }

    // Mock ConnectionFactory para cuando RabbitMQ no está disponible
    private static class MockConnectionFactory implements ConnectionFactory {
        @Override
        public org.springframework.amqp.rabbit.connection.Connection createConnection() {
            throw new RuntimeException("RabbitMQ no está disponible");
        }

        @Override
        public String getHost() {
            return "localhost";
        }

        @Override
        public int getPort() {
            return 5672;
        }

        @Override
        public String getVirtualHost() {
            return "/";
        }

        @Override
        public String getUsername() {
            return "guest";
        }

        @Override
        public void addConnectionListener(org.springframework.amqp.rabbit.connection.ConnectionListener listener) {
            // No-op
        }

        @Override
        public boolean removeConnectionListener(org.springframework.amqp.rabbit.connection.ConnectionListener listener) {
            return false;
        }

        @Override
        public void clearConnectionListeners() {
            // No-op
        }
    }
}
