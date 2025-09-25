package com.findataagg.alert.constants;

/**
 * Constants for alert system database table names.
 * This centralized approach ensures consistency and makes refactoring easier.
 */
public final class AlertTableConstants {

    private AlertTableConstants() {
        // Utility class - prevent instantiation
    }

    /**
     * Alert system tables
     */
    public static final String ALERT_RULES_TABLE = "alert_rules";
}