package com.alertprocessorservice.events;

import com.findataagg.common.messaging.model.PriceUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PriceListenerService {

    @RabbitListener(queues = "${app.rabbitmq.queue-name}")
    public void handlePriceUpdate(PriceUpdate priceUpdate) {
        log.info("Received price update from queue: {}", priceUpdate);
    }

}
