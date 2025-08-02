package com.dataingestorservice.config;

import com.findataagg.common.messaging.config.BaseRabbitMQConfig;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for data-ingestor-service.
 * Extends the base configuration to inherit common exchange setup.
 */
@Configuration
public class RabbitMQConfig extends BaseRabbitMQConfig {
    // Inherits the priceEventsTopicExchange bean from BaseRabbitMQConfig
    // Service-specific queues and bindings can be added here if needed
}
