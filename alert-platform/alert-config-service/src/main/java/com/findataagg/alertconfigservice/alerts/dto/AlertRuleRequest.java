package com.findataagg.alertconfigservice.alerts.dto;

import com.findataagg.alert.model.ConditionType;

import java.math.BigDecimal;

public record AlertRuleRequest(
        String symbol,
        ConditionType conditionType,
        BigDecimal value,
        String notes
) {
}
