package com.eval.conjunta.analyzer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String EVENTS_EXCHANGE = "events.exchange";
    public static final String SENSOR_QUEUE = "sensor.events.queue";
    public static final String ALERT_QUEUE = "alert.events.queue";
    public static final String SENSOR_ROUTING_KEY = "sensor.new";
    public static final String ALERT_ROUTING_KEY = "alert.created";
    
    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE);
    }
    
    @Bean
    public Queue sensorEventQueue() {
        return QueueBuilder.durable(SENSOR_QUEUE).build();
    }
    
    @Bean
    public Queue alertEventQueue() {
        return QueueBuilder.durable(ALERT_QUEUE).build();
    }
    
    @Bean
    public Binding sensorEventBinding() {
        return BindingBuilder
                .bind(sensorEventQueue())
                .to(eventsExchange())
                .with(SENSOR_ROUTING_KEY);
    }
    
    @Bean
    public Binding alertEventBinding() {
        return BindingBuilder
                .bind(alertEventQueue())
                .to(eventsExchange())
                .with(ALERT_ROUTING_KEY);
    }
    
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
