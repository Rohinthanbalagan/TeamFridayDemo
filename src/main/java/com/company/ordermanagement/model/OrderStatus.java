package com.company.ordermanagement.model;

/**
 * Enum representing the possible states of an order.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    PAYMENT_FAILED,
    REFUNDED
}
