package com.findataagg.common.messaging.model;

/**
 * Interface for all update events in the financial data aggregation system.
 * Provides a common contract for different types of market data updates.
 */
public interface UpdateEvent {
    
    /**
     * Returns the type of update event.
     * @return the event type (e.g., "price", "quote")
     */
    String type();
    
    /**
     * Returns the symbol for this update event.
     * @return the financial instrument symbol
     */
    String symbol();
}