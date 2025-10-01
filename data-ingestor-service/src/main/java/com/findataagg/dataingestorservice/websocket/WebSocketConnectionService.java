package com.findataagg.dataingestorservice.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.findataagg.common.messaging.service.UpdatePublishingService;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service responsible for establishing and managing WebSocket connections to Alpaca Markets.
 * Handles authentication flow and subscription management.
 */
@Slf4j
@Service
public class WebSocketConnectionService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UpdatePublishingService updatePublishingService;
    private final WebSocketReconnectionManager reconnectionManager;

    @Value("${alpaca.websocket-url}")
    private String webSocketUrl;

    @Value("${alpaca.api-key-id}")
    private String apiKeyId;

    @Value("${alpaca.secret-key}")
    private String secretKey;

    @Value("${app.stocks}")
    private List<String> stocks;

    @Value("${app.websocket.auth-timeout-seconds:10}")
    private int authTimeoutSeconds;

    private final AtomicReference<Session> currentSession = new AtomicReference<>();

    public WebSocketConnectionService(UpdatePublishingService updatePublishingService,
                                     WebSocketReconnectionManager reconnectionManager) {
        this.updatePublishingService = updatePublishingService;
        this.reconnectionManager = reconnectionManager;
        reconnectionManager.setConnectionService(this);
    }

    /**
     * Establish WebSocket connection, authenticate, and subscribe to symbols.
     * Thread-safe - can be called multiple times (for reconnection).
     */
    public void connect() throws Exception {
        // Close existing session if present
        closeCurrentSession();

        CountDownLatch authLatch = new CountDownLatch(1);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        AlpacaClientEndpoint endpoint = new AlpacaClientEndpoint(
            updatePublishingService,
            authLatch,
            reconnectionManager
        );

        log.info("Connecting to Alpaca at: {}", webSocketUrl);
        Session session = container.connectToServer(endpoint, new URI(webSocketUrl));
        currentSession.set(session);

        // Send authentication request
        AlpacaAuthRequest authRequest = new AlpacaAuthRequest("auth", apiKeyId, secretKey);
        String authMessage = objectMapper.writeValueAsString(authRequest);
        session.getBasicRemote().sendText(authMessage);
        log.info("Sent authentication request. Waiting for confirmation...");

        // Wait for authentication with timeout
        boolean authenticated = authLatch.await(authTimeoutSeconds, TimeUnit.SECONDS);

        if (authenticated) {
            log.info("Authentication confirmed. Subscribing to symbols...");
            // Send subscription request
            SubscriptionRequest subRequest = new SubscriptionRequest("subscribe", stocks, stocks);
            String subMessage = objectMapper.writeValueAsString(subRequest);
            session.getBasicRemote().sendText(subMessage);
            log.info("Subscribed to trades and quotes for: {}", stocks);

            // Reset reconnection manager on successful connection
            reconnectionManager.reset();
        } else {
            log.error("Authentication timed out after {} seconds", authTimeoutSeconds);
            closeCurrentSession();
            throw new Exception("Authentication timeout");
        }
    }

    /**
     * Close the current WebSocket session if it exists.
     */
    private void closeCurrentSession() {
        Session session = currentSession.getAndSet(null);
        if (session != null && session.isOpen()) {
            try {
                session.close();
                log.debug("Closed previous WebSocket session");
            } catch (Exception e) {
                log.warn("Error closing previous session: {}", e.getMessage());
            }
        }
    }

    /**
     * Get the current active session (for health checks).
     */
    public Session getCurrentSession() {
        return currentSession.get();
    }

    /**
     * Check if currently connected.
     */
    public boolean isConnected() {
        Session session = currentSession.get();
        return session != null && session.isOpen();
    }
}
