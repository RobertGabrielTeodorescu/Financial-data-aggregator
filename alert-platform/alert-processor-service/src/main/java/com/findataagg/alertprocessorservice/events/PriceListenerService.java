package com.findataagg.alertprocessorservice.events;

import com.findataagg.alert.model.AlertRule;
import com.findataagg.alert.model.AlertStatus;
import com.findataagg.alertprocessorservice.alert.service.AlertEvaluationService;
import com.findataagg.alertprocessorservice.alert.service.AlertRuleService;
import com.findataagg.alertprocessorservice.persistence.model.MarketDataId;
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
import com.findataagg.alert.repository.AlertRuleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PriceListenerService {

    private final QuoteRepository quoteRepository;
    private final TradeRepository tradeRepository;
    private final AlertRuleService alertRuleService;
    private final AlertEvaluationService alertEvaluationService;
    private final UpdatePublishingService updatePublishingService;
    private final UserRepository userRepository;
    private final AlertRuleRepository alertRuleRepository;

    @Value("${app.alerts.fallback-email:system-alerts@findataagg.com}")
    private String fallbackEmail;

    public PriceListenerService(QuoteRepository quoteRepository, TradeRepository tradeRepository, AlertRuleService alertRuleService, AlertEvaluationService alertEvaluationService, UpdatePublishingService updatePublishingService, UserRepository userRepository, AlertRuleRepository alertRuleRepository) {
        this.quoteRepository = quoteRepository;
        this.tradeRepository = tradeRepository;
        this.alertRuleService = alertRuleService;
        this.alertEvaluationService = alertEvaluationService;
        this.updatePublishingService = updatePublishingService;
        this.userRepository = userRepository;
        this.alertRuleRepository = alertRuleRepository;
    }

    @RabbitListener(queues = "${app.rabbitmq.trades-queue-name}")
    @Transactional
    public void handleTradeUpdate(PriceUpdate priceUpdate) {
        log.info("Received TRADE update from queue: {}", priceUpdate);
        Instant timestamp = Instant.parse(priceUpdate.timestamp());
        MarketDataId id = new MarketDataId(null, timestamp);
        Trade trade = Trade.builder()
                .id(id)
                .symbol(priceUpdate.symbol())
                .price(priceUpdate.price())
                .size(priceUpdate.size())
                .build();
        tradeRepository.save(trade);

        List<AlertRule> activeRules = alertRuleService.findActiveRulesBySymbol(priceUpdate.symbol());
        if (!activeRules.isEmpty()) {
            List<AlertRule> triggeredRules = alertEvaluationService.evaluate(priceUpdate, activeRules);

            if (!triggeredRules.isEmpty()) {
                // Batch fetch all user emails in a single query to avoid N+1 problem
                Set<Long> userIds = triggeredRules.stream()
                        .map(AlertRule::getUserId)
                        .collect(Collectors.toSet());

                Map<Long, String> userEmailMap = userRepository.findAllById(userIds)
                        .stream()
                        .collect(Collectors.toMap(User::getId, User::getEmail));

                triggeredRules.forEach(rule -> {
                    log.warn("!!! ALERT TRIGGERED !!! Rule ID: {}, Symbol: {}, Condition: {} {}",
                            rule.getId(), rule.getSymbol(), rule.getConditionType(), rule.getValue());

                    // Mark alert as FIRED to prevent re-triggering on subsequent trades
                    rule.setStatus(AlertStatus.FIRED);
                    alertRuleRepository.save(rule);

                    // Get user email from batch-fetched map (single query instead of N queries)
                    String userEmail = userEmailMap.getOrDefault(rule.getUserId(), fallbackEmail);

                    AlertTriggeredEvent event = new AlertTriggeredEvent(rule.getId(), userEmail, rule.getSymbol(), rule.getConditionType().toString(), rule.getValue(), priceUpdate.price(), rule.getNotes());
                    updatePublishingService.publishUpdate(event);
                });

                // Evict cache for this symbol after all alerts processed (only once, not per alert)
                alertRuleService.evictCacheForSymbol(priceUpdate.symbol());
            }
        }
    }

    @RabbitListener(queues = "${app.rabbitmq.quotes-queue-name}")
    public void handleQuoteUpdate(QuoteUpdate quoteUpdate) {
        log.info("Received QUOTE update from queue: {}", quoteUpdate);
        Instant timestamp = Instant.parse(quoteUpdate.timestamp());
        MarketDataId id = new MarketDataId(null, timestamp);
        Quote quote = Quote.builder()
                .id(id)
                .symbol(quoteUpdate.symbol())
                .askSize(quoteUpdate.askSize())
                .askPrice(quoteUpdate.askPrice())
                .bidPrice(quoteUpdate.bidPrice())
                .bidSize(quoteUpdate.bidSize())
                .build();
        quoteRepository.save(quote);
    }
}
