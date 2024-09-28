package com.tranhuy105.site.event;

import com.tranhuy105.common.constant.PaymentMethod;
import com.tranhuy105.common.entity.Order;
import lombok.Getter;

public record OrderCreatedEvent(Order order, PaymentMethod paymentMethod, String userIp) {
}
