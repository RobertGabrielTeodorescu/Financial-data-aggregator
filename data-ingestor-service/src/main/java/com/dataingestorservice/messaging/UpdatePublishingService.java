package com.dataingestorservice.messaging;

import com.findataagg.common.messaging.model.UpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.findataagg.common.messaging.config.BaseRabbitMQConfig.createRoutingKey;

@Service
@Slf4j
public class UpdatePublishingService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange-name}")
    private String exchangeName;

    public UpdatePublishingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUpdate(UpdateEvent updateEvent) {
        String routingKey = createRoutingKey(updateEvent.type(), updateEvent.symbol());
        rabbitTemplate.convertAndSend(exchangeName, routingKey, updateEvent);
        log.info("Published to exchange '{}' with routing key '{}': {}", exchangeName, routingKey, updateEvent);
    }
}
