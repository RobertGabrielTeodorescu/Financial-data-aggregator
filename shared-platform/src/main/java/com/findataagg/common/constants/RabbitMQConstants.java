package com.findataagg.common.constants;

/**
 * Constants for RabbitMQ configuration shared across all services.
 */
public final class RabbitMQConstants {

    private RabbitMQConstants() {
        // Utility class
    }

    /**
     * Default exchange name for price events
     */
    public static final String DEFAULT_EXCHANGE_NAME = "price.events.topic";

    /**
     * Routing key patterns
     */
    public static final class RoutingKeys {
        public static final String STOCK_PREFIX = "price.stock.";
        public static final String CRYPTO_PREFIX = "price.crypto.";
        public static final String ALL_STOCKS = "price.stock.#";
        public static final String ALL_CRYPTO = "price.crypto.#";
        public static final String ALL_PRICES = "price.#";

        private RoutingKeys() {
            // Utility class
        }
    }
}