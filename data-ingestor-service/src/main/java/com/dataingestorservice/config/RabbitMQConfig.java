package com.dataingestorservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange-name}")
    private String exchangeName;

    @Bean
    public TopicExchange priceEventsTopicExchange() {
        return new TopicExchange(exchangeName);
    }

}
