package com.findataagg.alertconfigservice.alerts.service;

import com.findataagg.alert.model.AlertRule;
import com.findataagg.alert.repository.AlertRuleRepository;
import com.findataagg.alertconfigservice.alerts.dto.AlertRuleRequest;
import com.findataagg.alertconfigservice.users.repository.UserRepository;
import com.findataagg.common.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertConfigService {

    private final AlertRuleRepository alertRuleRepository;
    private final UserRepository userRepository;

    public AlertConfigService(AlertRuleRepository alertRuleRepository, UserRepository userRepository) {
        this.alertRuleRepository = alertRuleRepository;
        this.userRepository = userRepository;
    }

    public List<AlertRule> getAlertRulesForUser(String username) {
        User user = findUserByUsername(username);
        return alertRuleRepository.findByUserId(user.getId());
    }

    public AlertRule createAlertRuleForUser(String username, AlertRuleRequest request) {
        User user = findUserByUsername(username);

        AlertRule newRule = new AlertRule();
        newRule.setUserId(user.getId());
        newRule.setSymbol(request.symbol());
        newRule.setConditionType(request.conditionType());
        newRule.setValue(request.value());
        newRule.setNotes(request.notes());
        newRule.setEnabled(true);

        return alertRuleRepository.save(newRule);
    }

    public void deleteAlertRuleForUser(String username, Long alertId) {
        User user = findUserByUsername(username);

        AlertRule alertRule = alertRuleRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert rule not found with id: " + alertId));

        // Verify ownership before deletion
        if (!alertRule.getUserId().equals(user.getId())) {
            throw new SecurityException("User does not have permission to delete this alert rule");
        }

        alertRuleRepository.deleteById(alertId);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }
}
