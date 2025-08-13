package com.findataagg.alertprocessorservice.alert.repository;

import com.findataagg.alertprocessorservice.alert.model.AlertRule;
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

}
