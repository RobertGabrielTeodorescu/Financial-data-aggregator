package com.findataagg.alertprocessorservice.alert.service;

import com.findataagg.alert.model.AlertRule;
import com.findataagg.alert.model.AlertStatus;
import com.findataagg.alert.repository.AlertRuleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.findataagg.alertprocessorservice.config.CacheConfig.ALERT_RULES_CACHE;

@Service
public class AlertRuleService {

    private final AlertRuleRepository alertRuleRepository;

    public AlertRuleService(AlertRuleRepository alertRuleRepository) {
        this.alertRuleRepository = alertRuleRepository;
    }

    /**
     * Finds all active alert rules for a specific symbol that are in PENDING status.
     * Only PENDING alerts can be triggered to prevent infinite notifications.
     * Results are cached to avoid repeated database queries on high-frequency trade updates.
     *
     * @param symbol The stock symbol to find rules for.
     * @return A list of PENDING alert rules for the symbol.
     */
    @Cacheable(value = ALERT_RULES_CACHE, key = "#symbol")
    public List<AlertRule> findActiveRulesBySymbol(String symbol) {
        return alertRuleRepository.findBySymbolAndStatus(symbol, AlertStatus.PENDING);
    }

    /**
     * Evicts the cached alert rules for a specific symbol.
     * Should be called when alerts are created, updated, or deleted for that symbol.
     *
     * @param symbol The stock symbol to evict from cache.
     */
    @CacheEvict(value = ALERT_RULES_CACHE, key = "#symbol")
    public void evictCacheForSymbol(String symbol) {
        // Cache eviction handled by annotation
    }

    /**
     * Evicts all cached alert rules.
     * Should be called when an alert's status changes or for bulk operations.
     */
    @CacheEvict(value = ALERT_RULES_CACHE, allEntries = true)
    public void evictAllCache() {
        // Cache eviction handled by annotation
    }
}
