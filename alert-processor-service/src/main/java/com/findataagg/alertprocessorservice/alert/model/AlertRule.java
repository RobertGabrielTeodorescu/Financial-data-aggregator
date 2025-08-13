package com.findataagg.alertprocessorservice.alert.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "alert_rules", indexes = {
        @Index(name = "idx_alertrule_symbol", columnList = "symbol")
})
@Data
@NoArgsConstructor
public class AlertRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ConditionType conditionType;

    @Column(nullable = false, precision = 15, scale = 5)
    private BigDecimal value;

    @Column(nullable = false)
    private boolean enabled = true;

    private String notes;
}
