package com.alertprocessorservice.events.config;

import com.findataagg.common.messaging.config.BaseRabbitMQConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig extends BaseRabbitMQConfig {

    @Value("${app.rabbitmq.queue-name}")
    private String queueName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public Queue stockPriceQueue() {
        return new Queue(queueName, true); // durable=true
    }

    @Bean
    public Binding stockPriceBinding(Queue stockPriceQueue, TopicExchange priceEventsTopicExchange) {
        return BindingBuilder.bind(stockPriceQueue)
                .to(priceEventsTopicExchange)
                .with(routingKey);
    }

}
