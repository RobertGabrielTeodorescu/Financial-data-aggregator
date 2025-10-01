package com.findataagg.dataingestorservice.config;

import com.findataagg.dataingestorservice.websocket.WebSocketConnectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@ConfigurationProperties(prefix = "app")
@Slf4j
public class WebSocketConfig {

    private final WebSocketConnectionService connectionService;

    public WebSocketConfig(WebSocketConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connectToWebsocket() {
        try {
            log.info("Initiating WebSocket connection to Alpaca...");
            connectionService.connect();
        } catch (Exception e) {
            log.error("Initial WebSocket connection failed. Reconnection manager will handle retries.", e);
        }
    }
}