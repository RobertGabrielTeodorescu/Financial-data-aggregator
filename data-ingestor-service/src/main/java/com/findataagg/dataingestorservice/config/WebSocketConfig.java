package com.findataagg.dataingestorservice.config;

import com.findataagg.common.messaging.service.UpdatePublishingService;
import com.findataagg.dataingestorservice.websocket.AlpacaAuthRequest;
import com.findataagg.dataingestorservice.websocket.AlpacaClientEndpoint;
import com.findataagg.dataingestorservice.websocket.SubscriptionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "app")
@Slf4j
public class WebSocketConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${alpaca.websocket-url}")
    private String webSocketUrl;

    @Value("${alpaca.api-key-id}")
    private String apiKeyId;

    @Value("${alpaca.secret-key}")
    private String secretKey;

    @Setter
    private List<String> stocks;

    private final UpdatePublishingService updatePublishingService;

    public WebSocketConfig(UpdatePublishingService updatePublishingService) {
        this.updatePublishingService = updatePublishingService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connectToWebsocket() {
        try {
            // 1. Create the latch
            CountDownLatch authLatch = new CountDownLatch(1);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();

            // 2. Pass the latch to the endpoint
            AlpacaClientEndpoint endpoint = new AlpacaClientEndpoint(updatePublishingService, authLatch);
            log.info("Connecting to Alpaca at: {}", webSocketUrl);
            Session session = container.connectToServer(endpoint, new URI(webSocketUrl));

            // 3. Send auth message
            AlpacaAuthRequest authRequest = new AlpacaAuthRequest("auth", apiKeyId, secretKey);
            String authMessage = objectMapper.writeValueAsString(authRequest);
            session.getBasicRemote().sendText(authMessage);
            log.info("Sent authentication request to Alpaca. Waiting for confirmation...");

            // 4. Wait for the latch to be opened (with a timeout)
            boolean authenticated = authLatch.await(10, TimeUnit.SECONDS);

            if (authenticated) {
                log.info("Authentication confirmed. Proceeding with subscription.");
                // 5. Send subscription message
                SubscriptionRequest subRequest = new SubscriptionRequest("subscribe", stocks, stocks);
                String subMessage = objectMapper.writeValueAsString(subRequest);
                session.getBasicRemote().sendText(subMessage);
                log.info("Sent subscription request for trades and quotes for: {}", stocks);
            } else {
                log.error("Authentication timed out. Closing session.");
                session.close();
            }
        } catch (Exception e) {
            log.error("WebSocket connection to Alpaca failed", e);
        }
    }
}