package com.findataagg.alert.model;

/**
 * Status of an alert rule in its lifecycle.
 * Used to track whether an alert has been triggered and prevent infinite notifications.
 */
public enum AlertStatus {
    /**
     * Alert is active and waiting to be triggered when conditions are met.
     */
    PENDING,

    /**
     * Alert has been triggered and notification sent.
     * Will not trigger again until reset to PENDING.
     */
    FIRED,

    /**
     * Alert is disabled by user and will not be evaluated.
     */
    DISABLED
}
