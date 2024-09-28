package com.tranhuy105.site.payment;

import com.tranhuy105.common.entity.Order;

public interface StockService {
    void reserveStock(Order order);
    void releaseStock(Order order);
}
