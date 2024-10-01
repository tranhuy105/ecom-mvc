package com.tranhuy105.site.payment;

import com.tranhuy105.common.constant.OrderStatus;
import com.tranhuy105.common.constant.PaymentMethod;
import com.tranhuy105.common.entity.Order;
import com.tranhuy105.site.dto.OrderDTO;
import com.tranhuy105.site.dto.OrderDetailDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    @Transactional
    Order createOrder(Integer customerId, Integer shippingAddressId, PaymentMethod paymentMethod, HttpServletRequest request);

    @Transactional
    void cancelOrder(Integer customerId, String orderNumber);

    Order updateOrderStatus(Order order, OrderStatus newStatus);

    @Transactional
    void confirmOrder(Order order);

    @Transactional
    void cancelOrder(Order order);

    Order findOrderByOrderNumber(String orderNumber);

    List<OrderDTO> findOrderByCustomerId(Integer customerId);

    OrderDetailDTO findOrderDetailByOrderNumber(Integer customerId, String orderNumber);

    void expireOrder(String orderNumber);
}
