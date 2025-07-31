package com.dataingestorservice.messaging.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

// Represents the structure of a trade update from Alpaca
public record PriceUpdate(@JsonProperty("S") String symbol,
                          @JsonProperty("p") BigDecimal price, @JsonProperty("s") long size,
                          @JsonProperty("t") String timestamp) {
}
