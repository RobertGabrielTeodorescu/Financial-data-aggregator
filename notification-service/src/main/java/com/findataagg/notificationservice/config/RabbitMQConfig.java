package com.findataagg.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.alerts-queue-name}")
    private String alertsQueueName;

    @Value("${app.rabbitmq.alerts-routing-key}")
    private String alertsRoutingKey;

    @Value("${app.rabbitmq.exchange-name}")
    private String exchangeName;

    @Bean
    public Queue alertsTriggeredQueue() {
        return new Queue(alertsQueueName, true);
    }

    @Bean
    public TopicExchange priceEventsTopicExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding alertsTriggeredBinding(Queue alertsTriggeredQueue, TopicExchange priceEventsTopicExchange) {
        return BindingBuilder.bind(alertsTriggeredQueue)
                .to(priceEventsTopicExchange)
                .with(alertsRoutingKey);
    }

}
