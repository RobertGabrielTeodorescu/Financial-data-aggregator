package com.findataagg.alertprocessorservice.persistence.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class BaseMarketDataEntity implements Serializable {

    @EmbeddedId
    private MarketDataId id;

    @Column(nullable = false, length = 32)
    private String symbol;
}