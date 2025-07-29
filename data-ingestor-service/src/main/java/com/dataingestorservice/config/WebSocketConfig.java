package com.dataingestorservice.config;

import com.dataingestorservice.messaging.PricePublishingService;
import com.dataingestorservice.websocket.FinnHubClientEndpoint;
import com.dataingestorservice.websocket.SubscriptionRequest;
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

@Configuration
@ConfigurationProperties(prefix = "app")
@Slf4j
public class WebSocketConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${finnhub.websocket-url}")
    private String webSocketUrl;

    @Setter
    private List<String> stocks;

    private final PricePublishingService pricePublishingService;

    public WebSocketConfig(PricePublishingService pricePublishingService) {
        this.pricePublishingService = pricePublishingService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void connectToWebsocket() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            FinnHubClientEndpoint endpoint = new FinnHubClientEndpoint(pricePublishingService);
            Session session = container.connectToServer(endpoint, new URI(webSocketUrl));
            for (String stock : stocks) {
                SubscriptionRequest subscriptionRequest = new SubscriptionRequest("subscribe", stock);
                String subscriptionMessage = objectMapper.writeValueAsString(subscriptionRequest);
                session.getBasicRemote().sendText(subscriptionMessage);
                log.info("Subscribed to websocket for subscription: {}", subscriptionMessage);
            }
        } catch (Exception e) {
            log.error("WebSocket error for websocket: {}", webSocketUrl, e);
        }
    }

}
