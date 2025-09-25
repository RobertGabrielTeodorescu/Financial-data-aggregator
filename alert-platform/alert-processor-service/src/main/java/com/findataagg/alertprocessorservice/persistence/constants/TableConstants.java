package com.findataagg.alertprocessorservice.persistence.constants;

/**
 * Constants for database table names used throughout the alert processor service.
 * This centralized approach ensures consistency and makes refactoring easier.
 */
public final class TableConstants {

    private TableConstants() {
        // Utility class - prevent instantiation
    }

    /**
     * Market data tables
     */
    public static final String TRADES_TABLE = "trades";
    public static final String QUOTES_TABLE = "quotes";

    /**
     * Alert system tables
     */
    public static final String ALERT_RULES_TABLE = "alert_rules";

    /**
     * Array of all partitioned table names for iteration
     */
    public static final String[] PARTITIONED_TABLES = {
        TRADES_TABLE,
        QUOTES_TABLE
    };
}