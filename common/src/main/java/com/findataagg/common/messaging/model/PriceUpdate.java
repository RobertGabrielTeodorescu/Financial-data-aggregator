package com.findataagg.common.messaging.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Represents a price update message for financial instruments.
 * This record is used across all services for consistent price data representation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PriceUpdate(@JsonProperty("S") String symbol,
                          @JsonProperty("p") BigDecimal price,
                          @JsonProperty("s") long size,
                          @JsonProperty("t") String timestamp) implements UpdateEvent {
    
    @Override
    public String type() {
        return "price";
    }
}