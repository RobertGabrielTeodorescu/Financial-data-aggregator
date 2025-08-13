package com.findataagg.alertprocessorservice.persistence.model;

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
@Table(name = "quotes", indexes = {
        @Index(name = "idx_quote_symbol_timestamp", columnList = "symbol, timestamp DESC")
})
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Quote extends BaseMarketDataEntity {

    @Column(nullable = false, precision = 15, scale = 5)
    private BigDecimal bidPrice;

    @Column(nullable = false)
    private long bidSize;

    @Column(nullable = false, precision = 15, scale = 5)
    private BigDecimal askPrice;

    @Column(nullable = false)
    private long askSize;

}
