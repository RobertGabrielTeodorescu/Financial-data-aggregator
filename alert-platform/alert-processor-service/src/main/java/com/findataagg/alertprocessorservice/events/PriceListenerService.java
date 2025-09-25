package com.findataagg.alertprocessorservice.events;

import com.findataagg.alert.model.AlertRule;
import com.findataagg.alertprocessorservice.alert.service.AlertEvaluationService;
import com.findataagg.alertprocessorservice.alert.service.AlertRuleService;
import com.findataagg.alertprocessorservice.persistence.model.Quote;
import com.findataagg.alertprocessorservice.persistence.model.Trade;
import com.findataagg.alertprocessorservice.persistence.repository.QuoteRepository;
import com.findataagg.alertprocessorservice.persistence.repository.TradeRepository;
import com.findataagg.common.messaging.model.AlertTriggeredEvent;
import com.findataagg.common.messaging.model.PriceUpdate;
import com.findataagg.common.messaging.model.QuoteUpdate;
import com.findataagg.common.messaging.service.UpdatePublishingService;
import com.findataagg.common.model.User;
import com.findataagg.alert.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class PriceListenerService {

    private final QuoteRepository quoteRepository;
    private final TradeRepository tradeRepository;
    private final AlertRuleService alertRuleService;
    private final AlertEvaluationService alertEvaluationService;
    private final UpdatePublishingService updatePublishingService;
    private final UserRepository userRepository;

    public PriceListenerService(QuoteRepository quoteRepository, TradeRepository tradeRepository, AlertRuleService alertRuleService, AlertEvaluationService alertEvaluationService, UpdatePublishingService updatePublishingService, UserRepository userRepository) {
        this.quoteRepository = quoteRepository;
        this.tradeRepository = tradeRepository;
        this.alertRuleService = alertRuleService;
        this.alertEvaluationService = alertEvaluationService;
        this.updatePublishingService = updatePublishingService;
        this.userRepository = userRepository;
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

        List<AlertRule> activeRules = alertRuleService.findActiveRulesBySymbol(priceUpdate.symbol());
        if (!activeRules.isEmpty()) {
            List<AlertRule> triggeredRules = alertEvaluationService.evaluate(priceUpdate, activeRules);
            triggeredRules.forEach(rule -> {
                log.warn("!!! ALERT TRIGGERED !!! Rule ID: {}, Symbol: {}, Condition: {} {}",
                        rule.getId(), rule.getSymbol(), rule.getConditionType(), rule.getValue());

                String userEmail = userRepository.findById(rule.getUserId())
                        .map(User::getEmail)
                        .orElse("unknown@example.com");

                AlertTriggeredEvent event = new AlertTriggeredEvent(rule.getId(), userEmail, rule.getSymbol(), rule.getConditionType().toString(), rule.getValue(), priceUpdate.price(), rule.getNotes());
                updatePublishingService.publishUpdate(event);
            });
        }
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
