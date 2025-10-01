package com.findataagg.alertprocessorservice.alert.service;

import com.findataagg.alert.model.AlertRule;
import com.findataagg.alert.model.AlertStatus;
import com.findataagg.alert.repository.AlertRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertRuleService {

    private final AlertRuleRepository alertRuleRepository;

    public AlertRuleService(AlertRuleRepository alertRuleRepository) {
        this.alertRuleRepository = alertRuleRepository;
    }

    /**
     * Finds all active alert rules for a specific symbol that are in PENDING status.
     * Only PENDING alerts can be triggered to prevent infinite notifications.
     *
     * @param symbol The stock symbol to find rules for.
     * @return A list of PENDING alert rules for the symbol.
     */
    public List<AlertRule> findActiveRulesBySymbol(String symbol) {
        return alertRuleRepository.findBySymbolAndStatus(symbol, AlertStatus.PENDING);
    }
}
