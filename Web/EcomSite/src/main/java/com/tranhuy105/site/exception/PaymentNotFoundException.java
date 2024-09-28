package com.tranhuy105.site.exception;
public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String orderNumber) {
        super("Payment for order " + orderNumber + " was not found");
    }
}