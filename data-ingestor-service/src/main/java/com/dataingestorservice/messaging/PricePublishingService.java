package com.dataingestorservice.messaging;

import com.dataingestorservice.messaging.model.PriceUpdate;
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

    public void publishPriceUpdate(PriceUpdate.TradeData tradeData) {
        String routingKey = createRoutingKey(tradeData.symbol());
        rabbitTemplate.convertAndSend(exchangeName, routingKey, tradeData);
        log.info("Published to exchange '{}' with routing key '{}': {}", exchangeName, routingKey, tradeData);
    }

    private String createRoutingKey(String symbol) {
        String simplifiedSymbol = symbol.toLowerCase().replace(":", ".");
        String type = simplifiedSymbol.contains("binance") ? "crypto" : "stock";
        return String.format("price.%s.%s", type, simplifiedSymbol);
    }
}
