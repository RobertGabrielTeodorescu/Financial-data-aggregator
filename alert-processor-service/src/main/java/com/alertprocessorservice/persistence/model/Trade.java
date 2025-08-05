package com.alertprocessorservice.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "trades", indexes = {
        @Index(name = "idx_trade_symbol_timestamp", columnList = "symbol, timestamp DESC")
})
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Trade extends BaseMarketDataEntity {

    @Column(nullable = false, precision = 15, scale = 5)
    private BigDecimal price;

    @Column(nullable = false)
    private long size;

}
