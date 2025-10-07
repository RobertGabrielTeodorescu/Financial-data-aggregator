package com.findataagg.alertprocessorservice.events;

import com.findataagg.alertprocessorservice.alert.service.AlertRuleService;
import com.findataagg.common.messaging.model.CacheInvalidationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Listens for cache invalidation events from alert-config-service
 * and evicts cached alert rules accordingly.
 */
@Slf4j
@Service
public class CacheInvalidationListener {

    private final AlertRuleService alertRuleService;

    public CacheInvalidationListener(AlertRuleService alertRuleService) {
        this.alertRuleService = alertRuleService;
    }

    @RabbitListener(queues = "${app.rabbitmq.cache-invalidation-queue-name}")
    public void handleCacheInvalidation(CacheInvalidationEvent event) {
        log.info("Received cache invalidation event - cacheName: {}, symbol: {}",
                event.cacheName(), event.symbol());

        if ("alertRules".equals(event.cacheName())) {
            if (event.symbol() == null) {
                // Null symbol means evict ALL cache entries
                alertRuleService.evictAllCache();
                log.info("Evicted all cache entries for alertRules cache");
            } else {
                // Specific symbol eviction
                alertRuleService.evictCacheForSymbol(event.symbol());
                log.debug("Evicted cache for symbol: {}", event.symbol());
            }
        }
    }
}
