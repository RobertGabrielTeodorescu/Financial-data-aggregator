package com.findataagg.dataingestorservice.websocket;

import java.util.List;

public record SubscriptionRequest(String action, List<String> trades, List<String> quotes) {
}
