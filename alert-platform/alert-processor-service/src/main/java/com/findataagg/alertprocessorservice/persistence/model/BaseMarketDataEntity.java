package com.findataagg.alertprocessorservice.persistence.model;

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
    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false, length = 32)
    private String symbol;
}