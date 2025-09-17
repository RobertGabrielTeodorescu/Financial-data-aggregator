package com.findataagg.alertconfigservice.controller;

import com.findataagg.alert.model.AlertRule;
import com.findataagg.alertconfigservice.dto.AlertRuleRequest;
import com.findataagg.alertconfigservice.service.AlertConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertRuleController {

    private final AlertConfigService alertConfigService;

    public AlertRuleController(AlertConfigService alertRuleService) {
        this.alertConfigService = alertRuleService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<AlertRule> createAlert(@PathVariable Long userId, @RequestBody AlertRuleRequest request) {
        AlertRule createdRule = alertConfigService.createAlertRuleForUser(userId, request);
        return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AlertRule>> getAlertsForUser(@PathVariable Long userId) {
        List<AlertRule> rules = alertConfigService.getAlertRulesForUser(userId);
        return ResponseEntity.ok(rules);
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long alertId) {
        // Again, we'd need a security check here
        alertConfigService.deleteAlertRule(alertId);
        return ResponseEntity.noContent().build();
    }
}
