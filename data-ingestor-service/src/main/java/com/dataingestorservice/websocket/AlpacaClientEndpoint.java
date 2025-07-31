package com.dataingestorservice.websocket;

import com.dataingestorservice.messaging.PricePublishingService;
import com.dataingestorservice.messaging.model.PriceUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@ClientEndpoint
@Slf4j
public class AlpacaClientEndpoint {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PricePublishingService publishingService;
    private final CountDownLatch authLatch;

    public AlpacaClientEndpoint(PricePublishingService publishingService, CountDownLatch authLatch) {
        this.publishingService = publishingService;
        this.authLatch = authLatch;
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("SUCCESS: Connected to Alpaca WebSocket endpoint: {}", session.getRequestURI());
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            // Alpaca sends an array of messages
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    handleNode(node);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing Alpaca message: {}", message, e);
        }
    }

    private void handleNode(JsonNode node) throws JsonProcessingException {
        if (!node.has("T")) return; // Guard clause

        String messageType = node.get("T").asText();
        switch (messageType) {
            case "success":
                if (node.has("msg") && "authenticated".equals(node.get("msg").asText())) {
                    log.info("Authentication successful. Opening latch.");
                    authLatch.countDown(); // Open the gate!
                }
                break;
            case "subscription":
                log.info("Alpaca subscription confirmation: {}", node);
                break;
            case "t":
                PriceUpdate update = objectMapper.treeToValue(node, PriceUpdate.class);
                publishingService.publishPriceUpdate(update);
                break;
            default:
                log.warn("Received unhandled Alpaca message type '{}'", messageType);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("WebSocket error for session: {}", session.getId(), throwable);
    }

}
