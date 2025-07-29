package com.dataingestorservice.websocket;

import com.dataingestorservice.messaging.PricePublishingService;
import com.dataingestorservice.messaging.model.PriceUpdate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import lombok.extern.slf4j.Slf4j;

@ClientEndpoint
@Slf4j
public class FinnHubClientEndpoint {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PricePublishingService publishingService;

    public FinnHubClientEndpoint(PricePublishingService publishingService) {
        this.publishingService = publishingService;
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("Connected to WebSocket endpoint: {}", session.getRequestURI());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            PriceUpdate priceUpdate = objectMapper.readValue(message, PriceUpdate.class);
            if ("trade".equals(priceUpdate.type()) && priceUpdate.data() != null) {
                priceUpdate.data().forEach(publishingService::publishPriceUpdate);
            } else if ("ping".equals(priceUpdate.type())) {
                log.debug("Received ping from server");
            }
        } catch (JsonProcessingException e) {
            log.error("Error while processing json message: {}", message, e);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("WebSocket error for session: {}", session.getId(), throwable);
    }

}
