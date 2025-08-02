package com.dataingestorservice.messaging;

import com.findataagg.common.messaging.model.PriceUpdate;
import com.findataagg.common.messaging.config.BaseRabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PricePublishingService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange-name}")
    private String exchangeName;

    public PricePublishingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPriceUpdate(PriceUpdate priceUpdate) {
        String routingKey = createRoutingKey(priceUpdate.symbol());
        rabbitTemplate.convertAndSend(exchangeName, routingKey, priceUpdate);
        log.info("Published to exchange '{}' with routing key '{}': {}", exchangeName, routingKey, priceUpdate);
    }

    private String createRoutingKey(String symbol) {
        return BaseRabbitMQConfig.createRoutingKey(symbol);
    }
}
