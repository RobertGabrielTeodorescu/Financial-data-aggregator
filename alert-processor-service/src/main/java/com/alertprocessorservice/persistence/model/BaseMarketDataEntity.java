package com.alertprocessorservice.persistence.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class BaseMarketDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String symbol;

    @Column(nullable = false)
    private Instant timestamp;
}