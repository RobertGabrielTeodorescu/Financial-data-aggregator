package com.alertprocessorservice.events;

import com.alertprocessorservice.persistence.model.Quote;
import com.alertprocessorservice.persistence.model.Trade;
import com.alertprocessorservice.persistence.repository.QuoteRepository;
import com.alertprocessorservice.persistence.repository.TradeRepository;
import com.findataagg.common.messaging.model.PriceUpdate;
import com.findataagg.common.messaging.model.QuoteUpdate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class PriceListenerService {

    private final QuoteRepository quoteRepository;
    private final TradeRepository tradeRepository;

    public PriceListenerService(QuoteRepository quoteRepository, TradeRepository tradeRepository) {
        this.quoteRepository = quoteRepository;
        this.tradeRepository = tradeRepository;
    }

    @RabbitListener(queues = "${app.rabbitmq.trades-queue-name}")
    @Transactional
    public void handleTradeUpdate(PriceUpdate priceUpdate) {
        log.info("Received TRADE update from queue: {}", priceUpdate);
        Trade trade = Trade.builder()
                .symbol(priceUpdate.symbol())
                .price(priceUpdate.price())
                .timestamp(Instant.parse(priceUpdate.timestamp()))
                .size(priceUpdate.size())
                .build();
        tradeRepository.save(trade);
    }

    @RabbitListener(queues = "${app.rabbitmq.quotes-queue-name}")
    public void handleQuoteUpdate(QuoteUpdate quoteUpdate) {
        log.info("Received QUOTE update from queue: {}", quoteUpdate);
        Quote quote = Quote.builder()
                .symbol(quoteUpdate.symbol())
                .askSize(quoteUpdate.askSize())
                .askPrice(quoteUpdate.askPrice())
                .bidPrice(quoteUpdate.bidPrice())
                .bidSize(quoteUpdate.bidSize())
                .timestamp(Instant.parse(quoteUpdate.timestamp()))
                .build();
        quoteRepository.save(quote);
    }
}
