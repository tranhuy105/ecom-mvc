package com.tranhuy105.site.exception;

public class PaymentRetryNotAllowedException extends RuntimeException {
    public PaymentRetryNotAllowedException(String message) {
        super(message);
    }
}
