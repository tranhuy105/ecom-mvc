package com.tranhuy105.site.event;

import com.tranhuy105.common.entity.Order;
import lombok.Getter;

public record OrderCancelledEvent(Order order) {
}
