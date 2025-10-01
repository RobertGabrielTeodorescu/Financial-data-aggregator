package com.findataagg.dataingestorservice.websocket;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages WebSocket reconnection with exponential backoff strategy.
 * Automatically restores WebSocket connections when lost due to network issues,
 * server restarts, or other failures.
 */
@Slf4j
@Component
public class WebSocketReconnectionManager {

    @Value("${app.websocket.reconnect.max-retries:10}")
    private int maxRetries;

    @Value("${app.websocket.reconnect.initial-backoff-ms:1000}")
    private long initialBackoffMs;

    @Value("${app.websocket.reconnect.max-backoff-ms:300000}")
    private long maxBackoffMs;

    @Value("${app.websocket.reconnect.jitter-percentage:0.1}")
    private double jitterPercentage;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicInteger retryCount = new AtomicInteger(0);
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);

    @Setter
    private WebSocketConnectionService connectionService;

    /**
     * Trigger reconnection attempt with exponential backoff.
     * Thread-safe and idempotent - multiple calls will not create multiple reconnection attempts.
     */
    public void scheduleReconnect(String reason) {
        if (!reconnecting.compareAndSet(false, true)) {
            log.debug("Reconnection already in progress, ignoring new request");
            return;
        }

        int attempt = retryCount.incrementAndGet();
        if (attempt > maxRetries) {
            log.error("Max reconnection attempts ({}) reached. Manual intervention required. Reason: {}",
                    maxRetries, reason);
            reconnecting.set(false);
            return;
        }

        long backoffMs = calculateBackoff(attempt);
        log.warn("WebSocket disconnected: {}. Scheduling reconnection attempt {}/{} in {}ms",
                reason, attempt, maxRetries, backoffMs);

        scheduler.schedule(() -> {
            try {
                reconnect();
            } finally {
                reconnecting.set(false);
            }
        }, backoffMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Calculate exponential backoff with jitter.
     * Formula: min(initial * 2^(attempt-1), max) + random_jitter
     */
    private long calculateBackoff(int attempt) {
        long exponentialBackoff = initialBackoffMs * (long) Math.pow(2, attempt - 1d);
        long cappedBackoff = Math.min(exponentialBackoff, maxBackoffMs);
        long jitter = (long) (cappedBackoff * jitterPercentage * Math.random());
        return cappedBackoff + jitter;
    }

    private void reconnect() {
        try {
            log.info("Attempting to reconnect to Alpaca WebSocket (attempt {}/{})",
                    retryCount.get(), maxRetries);
            connectionService.connect();
            retryCount.set(0);
            log.info("WebSocket reconnection successful!");
        } catch (Exception e) {
            log.error("Reconnection attempt {} failed: {}", retryCount.get(), e.getMessage());
            scheduleReconnect("Reconnection failed: " + e.getMessage());
        }
    }

    public void reset() {
        retryCount.set(0);
        reconnecting.set(false);
        log.debug("Reconnection manager state reset");
    }

    public void shutdown() {
        log.info("Shutting down reconnection manager");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
