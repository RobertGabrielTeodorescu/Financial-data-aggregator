package com.findataagg.common.messaging.model;


/**
 * Event to signal cache invalidation across services.
 * Used when alert rules are created, updated, or deleted in alert-config-service
 * to notify alert-processor-service to evict cached rules.
 *
 * If symbol is null, this indicates a full cache eviction (all entries).
 */
public record CacheInvalidationEvent(
        String cacheName,
        String symbol  // null means evict ALL entries for this cache
) implements UpdateEvent {

    @Override
    public String type() {
        return "cache.invalidation";
    }
}
