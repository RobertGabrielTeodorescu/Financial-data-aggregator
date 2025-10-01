package com.findataagg.alert.repository;

import com.findataagg.alert.model.AlertRule;
import com.findataagg.alert.model.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {

    /**
     * Finds all enabled alert rules for a specific symbol.
     *
     * @param symbol  The stock symbol to find rules for.
     * @param enabled The status of the rule.
     * @return A list of active alert rules.
     */
    List<AlertRule> findBySymbolAndEnabled(String symbol, boolean enabled);

    /**
     * Finds all alert rules for a specific symbol with a given status.
     *
     * @param symbol The stock symbol to find rules for.
     * @param status The status of the alert rule.
     * @return A list of alert rules matching the criteria.
     */
    List<AlertRule> findBySymbolAndStatus(String symbol, AlertStatus status);

    // Find all rules for a specific user
    List<AlertRule> findByUserId(Long userId);

}