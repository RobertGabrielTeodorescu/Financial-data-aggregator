package com.dataingestorservice.messaging.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

// Represents the structure of a trade update from Finnhub
public record PriceUpdate(String type, List<TradeData> data) {
    public record TradeData(
            @JsonProperty("s") String symbol,
            @JsonProperty("p") BigDecimal price,
            @JsonProperty("t") long timestamp,
            @JsonProperty("v") BigDecimal volume
    ) {}
}
