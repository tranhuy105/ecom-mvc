package com.tranhuy105.common.constant;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    IN_TRANSIT,
    DELIVERED,
    CANCELED,
    EXPIRED,
    RETURNED;


    public boolean canTransit(OrderStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == CONFIRMED || newStatus == CANCELED || newStatus == EXPIRED;
            case CONFIRMED -> newStatus == PREPARING || newStatus == CANCELED;
            case PREPARING -> newStatus == IN_TRANSIT || newStatus == CANCELED;
            case IN_TRANSIT -> newStatus == DELIVERED || newStatus == RETURNED;
            case DELIVERED -> newStatus == RETURNED;
            case CANCELED, EXPIRED -> false;
            case RETURNED -> false;
            default -> false;
        };
    }
}
