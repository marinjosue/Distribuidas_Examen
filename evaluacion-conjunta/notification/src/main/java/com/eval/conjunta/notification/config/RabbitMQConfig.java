package  com.eval.conjunta.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String EVENTS_EXCHANGE = "events.exchange";
    public static final String ALERT_QUEUE = "alert.events.queue";
    public static final String SENSOR_INACTIVE_QUEUE = "sensor.inactive.queue";
    public static final String DAILY_REPORT_QUEUE = "daily.report.queue";
    
    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE);
    }
    
    @Bean
    public Queue alertEventQueue() {
        return QueueBuilder.durable(ALERT_QUEUE).build();
    }
    
    @Bean
    public Queue sensorInactiveQueue() {
        return QueueBuilder.durable(SENSOR_INACTIVE_QUEUE).build();
    }
    
    @Bean
    public Queue dailyReportQueue() {
        return QueueBuilder.durable(DAILY_REPORT_QUEUE).build();
    }
    
    @Bean
    public Binding alertEventBinding() {
        return BindingBuilder
                .bind(alertEventQueue())
                .to(eventsExchange())
                .with("alert.created");
    }
    
    @Bean
    public Binding sensorInactiveBinding() {
        return BindingBuilder
                .bind(sensorInactiveQueue())
                .to(eventsExchange())
                .with("sensor.inactive");
    }
    
    @Bean
    public Binding dailyReportBinding() {
        return BindingBuilder
                .bind(dailyReportQueue())
                .to(eventsExchange())
                .with("report.daily");
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
