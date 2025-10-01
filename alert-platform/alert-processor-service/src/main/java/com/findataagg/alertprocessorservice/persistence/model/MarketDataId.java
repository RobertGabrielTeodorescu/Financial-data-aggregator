package com.findataagg.alertprocessorservice.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * Composite primary key class for partitioned market data entities (Trade, Quote).
 * Uses both timestamp and auto-incrementing id to ensure uniqueness even when
 * multiple records occur at the same microsecond/nanosecond.
 *
 * This prevents data loss in high-frequency trading scenarios where trades
 * can arrive at identical timestamps.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketDataId implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Instant timestamp;
}
