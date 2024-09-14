package com.tranhuy105.site.payment;

import com.tranhuy105.common.constant.OrderStatus;
import com.tranhuy105.common.entity.Order;
import com.tranhuy105.site.dto.OrderDTO;
import com.tranhuy105.site.dto.OrderDetailDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    @Transactional
    Order createOrder(Integer customerId, Integer shippingAddressId);

    @Transactional
    Order createCodOrder(Integer customerId, Integer shippingAddressId);

    @Transactional
    void cancelOrder(Integer customerId, String orderNumber);

    void updateOrderStatus(Order order, OrderStatus newStatus);

    List<OrderDTO> findOrderByCustomerId(Integer customerId);

    OrderDetailDTO findOrderDetailByOrderNumber(Integer customerId, String orderNumber);

}
