package com.findataagg.common.messaging.config;

import com.findataagg.common.constants.RabbitMQConstants;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Base RabbitMQ configuration providing common exchange setup.
 * Services can extend this configuration to add service-specific queues and bindings.
 */
@Configuration
public class BaseRabbitMQConfig {

    @Value("${app.rabbitmq.exchange-name:" + RabbitMQConstants.DEFAULT_EXCHANGE_NAME + "}")
    private String exchangeName;

    /**
     * Creates the main topic exchange for price events.
     * All services will publish to and consume from this exchange.
     */
    @Bean
    public TopicExchange priceEventsTopicExchange() {
        return new TopicExchange(exchangeName);
    }

    /**
     * Utility method to determine routing key based on symbol
     */
    public static String createRoutingKey(String type, String symbol) {
        String simplifiedSymbol = symbol.toLowerCase().replace(":", ".");
        String assetType = simplifiedSymbol.contains("binance") ? "crypto" : "stock";
        return String.format("%s.%s.%s", type, assetType, simplifiedSymbol);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}