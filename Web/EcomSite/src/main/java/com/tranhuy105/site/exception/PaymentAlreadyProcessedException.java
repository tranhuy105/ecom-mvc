package com.tranhuy105.site.exception;

public class PaymentAlreadyProcessedException extends RuntimeException {
    public PaymentAlreadyProcessedException(String orderNumber) {
        super("Payment for order " + orderNumber + " has already been processed.");
    }
}