package com.findataagg.alert.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.findataagg.alert.constants.AlertTableConstants.ALERT_RULES_TABLE;

@Entity
@Table(name = ALERT_RULES_TABLE)
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertStatus status = AlertStatus.PENDING;

    private String notes;

    @Column(nullable = false)
    private Long userId;
}