package com.tranhuy105.site.event;

import com.tranhuy105.common.entity.Customer;

public record PaymentInitiatedEvent(Customer customer, String redirectURL) {
}
