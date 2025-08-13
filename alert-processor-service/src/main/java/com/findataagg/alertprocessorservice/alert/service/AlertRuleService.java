package com.findataagg.alertprocessorservice.alert.service;

import com.findataagg.alertprocessorservice.alert.model.AlertRule;
import com.findataagg.alertprocessorservice.alert.repository.AlertRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertRuleService {

    private final AlertRuleRepository alertRuleRepository;

    public AlertRuleService(AlertRuleRepository alertRuleRepository) {
        this.alertRuleRepository = alertRuleRepository;
    }

    public List<AlertRule> findActiveRulesBySymbol(String symbol) {
        return alertRuleRepository.findBySymbolAndEnabled(symbol, true);
    }
}
