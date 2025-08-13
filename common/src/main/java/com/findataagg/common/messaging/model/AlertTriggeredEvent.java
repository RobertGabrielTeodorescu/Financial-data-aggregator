package com.findataagg.common.messaging.model;

import java.math.BigDecimal;

/**
 * Event published when an alert rule's conditions are met.
 */
public record AlertTriggeredEvent(
        Long ruleId,
        String symbol,
        String condition, // e.g., "GREATER_THAN"
        BigDecimal triggerValue,
        BigDecimal actualValue,
        String notes
) implements UpdateEvent {

    @Override
    public String type() {
        return "alert.triggered";
    }
}
