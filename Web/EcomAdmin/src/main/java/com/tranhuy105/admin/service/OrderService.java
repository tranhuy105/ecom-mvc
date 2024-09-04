package com.tranhuy105.admin.service;

import com.tranhuy105.admin.dto.OrderItemDTO;
import com.tranhuy105.admin.dto.OrderOverviewDTO;
import com.tranhuy105.admin.dto.ghn.GhnOrderResponse;
import com.tranhuy105.common.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    @Transactional
    void confirmOrder(Integer orderId);
    @Transactional
    GhnOrderResponse.Data prepareOrder(Integer orderId, String wardCode, Integer districtId);

    @Transactional
    void shipOrder(Integer orderId);

    @Transactional
    void deliverOrder(Integer orderId);

    Page<OrderOverviewDTO> listOrders(String orderStatus, String shippingStatus, String paymentStatus, LocalDateTime startDate, LocalDateTime endDate, String search, Pageable pageable);

    Order getOrderDetails(String orderNumber);

    List<OrderItemDTO> getOrderItems(Integer orderId);

    String printA5ShippingLabel(Integer orderId);
}
