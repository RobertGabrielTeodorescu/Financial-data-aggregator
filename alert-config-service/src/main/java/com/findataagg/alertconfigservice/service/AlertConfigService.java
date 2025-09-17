package com.findataagg.alertconfigservice.service;

import com.findataagg.alert.model.AlertRule;
import com.findataagg.alert.repository.AlertRuleRepository;
import com.findataagg.alertconfigservice.dto.AlertRuleRequest;

import java.util.List;

public class AlertConfigService {

    private final AlertRuleRepository alertRuleRepository;

    public AlertConfigService(AlertRuleRepository alertRuleRepository) {
        this.alertRuleRepository = alertRuleRepository;
    }

    public List<AlertRule> getAlertRulesForUser(Long userId) {
        return alertRuleRepository.findByUserId(userId);
    }

    public AlertRule createAlertRuleForUser(Long userId, AlertRuleRequest request) {
        AlertRule newRule = new AlertRule();
        newRule.setUserId(userId);
        newRule.setSymbol(request.symbol());
        newRule.setConditionType(request.conditionType());
        newRule.setValue(request.value());
        newRule.setNotes(request.notes());
        newRule.setEnabled(true);

        return alertRuleRepository.save(newRule);
    }

    public void deleteAlertRule(Long alertId) {
        // In a real app, we'd add a check here to ensure the user
        // has permission to delete this alert.
        alertRuleRepository.deleteById(alertId);
    }

}
