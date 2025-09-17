package com.findataagg.alertprocessorservice.alert.service;

import com.findataagg.alert.model.AlertRule;
import com.findataagg.common.messaging.model.PriceUpdate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertEvaluationService {

    /**
     * Evaluates a price update against a list of alert rules.
     *
     * @param priceUpdate The incoming price update.
     * @param rules       The list of rules to check against.
     * @return A list of rules that have been triggered.
     */
    public List<AlertRule> evaluate(PriceUpdate priceUpdate, List<AlertRule> rules) {
        return rules.stream()
                .filter(rule -> isTriggered(priceUpdate, rule))
                .collect(Collectors.toList());
    }

    private boolean isTriggered(PriceUpdate priceUpdate, AlertRule alertRule) {
        int comparison = priceUpdate.price().compareTo(alertRule.getValue());
        return switch (alertRule.getConditionType()) {
            case GREATER_THAN -> comparison > 0;
            case LESS_THAN -> comparison < 0;
        };
    }

}
