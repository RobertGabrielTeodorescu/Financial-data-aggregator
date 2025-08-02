package com.alertprocessorservice.events;

import com.findataagg.common.messaging.model.PriceUpdate;
import com.findataagg.common.messaging.model.QuoteUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PriceListenerService {

    @RabbitListener(queues = "${app.rabbitmq.trades-queue-name}")
    public void handleTradeUpdate(PriceUpdate priceUpdate) {
        log.info("Received TRADE update from queue: {}", priceUpdate);
    }

    @RabbitListener(queues = "${app.rabbitmq.quotes-queue-name}")
    public void handleQuoteUpdate(QuoteUpdate quoteUpdate) {
        log.info("Received QUOTE update from queue: {}", quoteUpdate);
    }
}
