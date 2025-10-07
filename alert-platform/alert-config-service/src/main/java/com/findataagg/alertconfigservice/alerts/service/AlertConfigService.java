package com.findataagg.alertconfigservice.alerts.service;

import com.findataagg.alert.model.AlertRule;
import com.findataagg.alert.model.AlertStatus;
import com.findataagg.alert.repository.AlertRuleRepository;
import com.findataagg.alertconfigservice.alerts.dto.AlertRuleRequest;
import com.findataagg.common.messaging.model.CacheInvalidationEvent;
import com.findataagg.common.messaging.service.UpdatePublishingService;
import com.findataagg.common.model.User;
import com.findataagg.alert.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertConfigService {

    private final AlertRuleRepository alertRuleRepository;
    private final UserRepository userRepository;
    private final UpdatePublishingService updatePublishingService;

    public AlertConfigService(AlertRuleRepository alertRuleRepository, UserRepository userRepository, UpdatePublishingService updatePublishingService) {
        this.alertRuleRepository = alertRuleRepository;
        this.userRepository = userRepository;
        this.updatePublishingService = updatePublishingService;
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
        newRule.setStatus(AlertStatus.PENDING);  // Set initial status to PENDING

        AlertRule savedRule = alertRuleRepository.save(newRule);

        // Publish cache invalidation event to notify alert-processor-service
        publishCacheInvalidation(savedRule.getSymbol());

        return savedRule;
    }

    public void deleteAlertRuleForUser(String username, Long alertId) {
        User user = findUserByUsername(username);

        AlertRule alertRule = alertRuleRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert rule not found with id: " + alertId));

        // Verify ownership before deletion
        if (!alertRule.getUserId().equals(user.getId())) {
            throw new SecurityException("User does not have permission to delete this alert rule");
        }

        String symbol = alertRule.getSymbol();
        alertRuleRepository.deleteById(alertId);

        // Publish cache invalidation event to notify alert-processor-service
        publishCacheInvalidation(symbol);
    }

    /**
     * Deletes all alert rules for a user.
     * Used for bulk operations or user account cleanup.
     *
     * @param username The username whose alerts should be deleted
     * @return The number of alerts deleted
     */
    public int deleteAllAlertsForUser(String username) {
        User user = findUserByUsername(username);
        List<AlertRule> userAlerts = alertRuleRepository.findByUserId(user.getId());
        int count = userAlerts.size();

        if (count > 0) {
            alertRuleRepository.deleteAll(userAlerts);

            // Evict entire cache since multiple symbols may be affected
            publishCacheInvalidationForAll();
        }

        return count;
    }

    private void publishCacheInvalidation(String symbol) {
        CacheInvalidationEvent event = new CacheInvalidationEvent("alertRules", symbol);
        updatePublishingService.publishUpdate(event);
    }

    private void publishCacheInvalidationForAll() {
        // Null symbol indicates full cache eviction
        CacheInvalidationEvent event = new CacheInvalidationEvent("alertRules", null);
        updatePublishingService.publishUpdate(event);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }
}
