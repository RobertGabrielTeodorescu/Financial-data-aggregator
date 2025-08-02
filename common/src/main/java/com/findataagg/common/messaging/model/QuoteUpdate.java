package com.findataagg.common.messaging.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuoteUpdate(
        @JsonProperty("S") String symbol,
        @JsonProperty("bp") BigDecimal bidPrice,
        @JsonProperty("bs") long bidSize,
        @JsonProperty("ap") BigDecimal askPrice,
        @JsonProperty("as") long askSize,
        @JsonProperty("t") String timestamp
) implements UpdateEvent {
    
    @Override
    public String type() {
        return "quote";
    }
}
