package com.findataagg.alertconfigservice.alerts.controller;

import com.findataagg.alert.model.AlertRule;
import com.findataagg.alertconfigservice.alerts.dto.AlertRuleRequest;
import com.findataagg.alertconfigservice.alerts.service.AlertConfigService;
import com.findataagg.alertconfigservice.util.SecurityUtils;
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

    @PostMapping
    public ResponseEntity<AlertRule> createAlert(@RequestBody AlertRuleRequest request) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        AlertRule createdRule = alertConfigService.createAlertRuleForUser(currentUsername, request);
        return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AlertRule>> getAlertsForCurrentUser() {
        String currentUsername = SecurityUtils.getCurrentUsername();
        List<AlertRule> rules = alertConfigService.getAlertRulesForUser(currentUsername);
        return ResponseEntity.ok(rules);
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long alertId) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        alertConfigService.deleteAlertRuleForUser(currentUsername, alertId);
        return ResponseEntity.noContent().build();
    }
}
