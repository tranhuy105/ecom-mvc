package com.tranhuy105.site.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderNumber) {
        super("Order with number " + orderNumber + " was not found.");
    }
}