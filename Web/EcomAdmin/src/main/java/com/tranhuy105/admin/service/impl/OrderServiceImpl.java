package com.tranhuy105.admin.service.impl;

import com.tranhuy105.admin.dto.OrderItemDTO;
import com.tranhuy105.admin.dto.OrderOverviewDTO;
import com.tranhuy105.admin.dto.ghn.GhnOrderRequest;
import com.tranhuy105.admin.dto.ghn.GhnOrderResponse;
import com.tranhuy105.admin.repository.OrderItemRepository;
import com.tranhuy105.admin.repository.OrderRepository;
import com.tranhuy105.admin.service.GhnApiService;
import com.tranhuy105.admin.service.OrderService;
import com.tranhuy105.common.constant.OrderStatus;
import com.tranhuy105.common.constant.PaymentStatus;
import com.tranhuy105.common.constant.ShippingStatus;
import com.tranhuy105.common.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final GhnApiService ghnApiService;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    @Override
    public void confirmOrder(Integer orderId) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new IllegalArgumentException("Invalid Order Id")
        );

        if (!order.getStatus().canTransit(OrderStatus.CONFIRMED)) {
            throw new IllegalStateException("Cannot confirm this order. Invalid status transition.");
        }

        updateOrderStatus(order, OrderStatus.CONFIRMED);
    }

    @Transactional
    public void updateOrderStatus(Order order, OrderStatus newStatus) {
        if (!order.getStatus().canTransit(newStatus)) {
            throw new IllegalStateException("Cannot transition from " + order.getStatus() + " to " + newStatus);
        }

        order.setStatus(newStatus);
        OrderStatusHistory statusHistory = new OrderStatusHistory();
        statusHistory.setOrder(order);
        statusHistory.setStatus(newStatus.name());
        order.getStatusHistory().add(statusHistory);

        switch (newStatus) {
            case CANCELED:
                order.setPaymentStatus(PaymentStatus.FAILED);
                order.getPayment().setStatus(PaymentStatus.FAILED);
                order.setShippingStatus(ShippingStatus.CANCELED);
                break;

            case DELIVERED:
                order.setShippingStatus(ShippingStatus.DELIVERED);
                order.setPaymentStatus(PaymentStatus.PAID);
                break;

            case PREPARING:
                order.setShippingStatus(ShippingStatus.PENDING);
                break;

            case IN_TRANSIT:
                order.setShippingStatus(ShippingStatus.IN_TRANSIT);
                break;

            default:
                break;
        }

        orderRepository.save(order);
    }

    @Transactional
    @Override
    public GhnOrderResponse.Data prepareOrder(Integer orderId, String wardCode, Integer districtId) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new IllegalArgumentException("Invalid Order Id")
        );

        if (!order.getStatus().canTransit(OrderStatus.PREPARING)) {
            throw new IllegalStateException("Cannot confirm this order. Invalid status transition.");
        }

        GhnOrderRequest request = buildGhnOrderRequest(order, wardCode, districtId);
        GhnOrderResponse response = ghnApiService.createOrder(request);

        if (response.getCode() == 200) {
            order.setShippingOrderCode(response.getData().getOrder_code());
            order.setExpectedDeliveryTime(response.getData().getExpected_delivery_time());
            order.setShippingAmount(new BigDecimal(response.getData().getTotal_fee()));
            order.setShippingStatus(ShippingStatus.IN_TRANSIT);
            updateOrderStatus(order, OrderStatus.PREPARING);
            return response.getData();
        } else {
            throw new RuntimeException("Failed to create GHN order: " + response.getMessage());
        }
    }

    @Transactional
    @Override
    public void shipOrder(Integer orderId) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new IllegalArgumentException("Invalid Order Id")
        );

        updateOrderStatus(order, OrderStatus.IN_TRANSIT);
    }

    @Transactional
    @Override
    public void deliverOrder(Integer orderId) {
        Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                () -> new IllegalArgumentException("Invalid Order Id")
        );

        updateOrderStatus(order, OrderStatus.DELIVERED);
    }

    @Override
    public Page<OrderOverviewDTO> listOrders(String orderStatus, String shippingStatus, String paymentStatus, LocalDateTime startDate, LocalDateTime endDate, String search, Pageable pageable) {
        if ("".equals(orderStatus)) {
            orderStatus = null;
        }

        if ("".equals(shippingStatus)) {
            shippingStatus = null;
        }

        if ("".equals(paymentStatus)) {
            paymentStatus = null;
        }

        if ("".equals(search)) {
            search = null;
        }

        return orderRepository.searchOrders(
                orderStatus != null ? OrderStatus.valueOf(orderStatus) : null,
                shippingStatus != null ? ShippingStatus.valueOf(shippingStatus) : null,
                paymentStatus != null ? PaymentStatus.valueOf(paymentStatus) : null,
                startDate, endDate, search, pageable);
    }

    private Order findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber).orElse(null);
    }

    @Override
    public Order getOrderDetails(String orderNumber) {
        return orderRepository.findOrderDetailByOrderNumber(orderNumber);
    }

    @Override
    public List<OrderItemDTO> getOrderItems(Integer orderId) {
        return orderItemRepository.findFullByOrderId(orderId);
    }

    private GhnOrderRequest buildGhnOrderRequest(Order order, String wardCode, Integer districtId) {
        GhnOrderRequest request = new GhnOrderRequest();

        request.setPayment_type_id(2);
        request.setFrom_name("Trần Huy 105 Shop");
        request.setFrom_phone("0342880966");
        request.setFrom_address("Dia Chi Cua Toi Test");
        request.setFrom_ward_name("Phường Xuân La");
        request.setFrom_district_name("Quận Tây Hồ");
        request.setFrom_province_name("Hà Nội");

        Address shippingAddress = order.getShippingAddress();
        request.setTo_name(order.getCustomer().getFullName());
        request.setTo_phone(order.getCustomer().getPhoneNumber());
        request.setTo_address(shippingAddress.toString());
        request.setTo_ward_code(wardCode);
        request.setTo_district_id(districtId);
        request.setCod_amount(order.getTotalAmount().intValue());
        request.setRequired_note("KHONGCHOXEMHANG");
        request.setService_type_id(2);

        List<GhnOrderRequest.Item> items = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            Sku sku = orderItem.getSku();
            GhnOrderRequest.Item item = new GhnOrderRequest.Item();
            item.setName(sku.getProduct().getName());
            item.setCode(sku.getSkuCode());
            item.setQuantity(orderItem.getQuantity());
            item.setPrice(sku.getPrice().intValue());
            item.setLength(sku.getLength().intValue());
            item.setWidth(sku.getWidth().intValue());
            item.setHeight(sku.getHeight().intValue());
            item.setWeight(sku.getWeight().intValue());
            items.add(item);
        }
        request.setItems(items);

        request.setWeight(order.getOrderItems().stream().mapToInt(item -> item.getQuantity() * item.getSku().getWeight().intValue()).sum());
        request.setLength(order.getOrderItems()
                .stream()
                .mapToInt(item -> item.getSku().getLength().intValue())
                .max()
                .orElse(0));

        request.setWidth(order.getOrderItems()
                .stream()
                .mapToInt(item -> item.getSku().getWidth().intValue())
                .max()
                .orElse(0));

        request.setHeight(order.getOrderItems()
                .stream()
                .mapToInt(item -> item.getSku().getHeight().intValue())
                .max()
                .orElse(0));


        return request;
    }
}
