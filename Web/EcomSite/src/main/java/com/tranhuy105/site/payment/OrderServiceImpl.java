package com.tranhuy105.site.payment;

import com.tranhuy105.common.constant.OrderStatus;
import com.tranhuy105.common.constant.PaymentStatus;
import com.tranhuy105.common.constant.ShippingStatus;
import com.tranhuy105.common.entity.*;
import com.tranhuy105.site.dto.*;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.repository.OrderItemRepository;
import com.tranhuy105.site.repository.OrderRepository;
import com.tranhuy105.site.repository.CustomerRepository;
import com.tranhuy105.site.repository.SkuRepository;
import com.tranhuy105.site.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ShoppingCartService cartService;
    private final SkuRepository skuRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Order createOrder(Integer customerId, Integer shippingAddressId) {
        Customer customer = validateCustomer(customerId);
        Address shippingAddress = validateShippingAddress(customer, shippingAddressId);
        ShoppingCart cart = validateCart(customerId);
        Order order = buildOrder(customer, shippingAddress, cart);
//        reserveOrder(order);
        order.setReservationExpiry(LocalDateTime.now().plusMinutes(15));
        updateOrderStatus(order, OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    private void reserveOrder(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Sku sku = orderItem.getSku();
            int newQuantity = sku.getStockQuantity() - orderItem.getQuantity();

            if (newQuantity < 0) {
                throw new IllegalArgumentException("Not enough stock for SKU: " + sku.getSkuCode());
            }

            sku.setStockQuantity(newQuantity);
            skuRepository.save(sku);
        }
    }

    @Override
    public Order findOrderById(Integer orderId) {
//        return orderRepository.findByIdWithPayment(orderId).orElse(null);
        return null;
    }

    @Override
    @Transactional
    public Order createCodOrder(Integer customerId, Integer shippingAddressId) {
        Customer customer = validateCustomer(customerId);
        Address shippingAddress = validateShippingAddress(customer, shippingAddressId);
        ShoppingCart cart = validateCart(customer.getId());
        for (CartItem cartItem : cart.getCartItems()) {
            if (!cartItem.getSku().getProduct().isSupportCod()) {
                throw new IllegalArgumentException("This product dont support COD method");
            }
        }
        Order order = buildOrder(customer, shippingAddress, cart);
//        reserveOrder(order);
        order.setReservationExpiry(LocalDateTime.now().plusDays(3));
        updateOrderStatus(order, OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);
        cartService.clearShoppingCart(customer.getId());
        return savedOrder;
    }

    @Override
    public void cancelOrder(Integer customerId, String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new AccessDeniedException("You dont have the permission to perform this action");
        }

        if (order.getPaymentStatus().equals(PaymentStatus.PAID)) {
            throw new IllegalArgumentException("You can not cancel an paid order, please contact the support team if need more assistant");
        }

        if (order.getStatus().equals(OrderStatus.PENDING)
                || order.getStatus().equals(OrderStatus.CONFIRMED)) {
            updateOrderStatus(order, OrderStatus.CANCELED);
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("You can not cancel order at this stage");
        }
    }

    @Override
    public void updateOrderStatus(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(newStatus.name());
        order.getStatusHistory().add(history);
    }

    @Override
    public List<OrderDTO> findOrderByCustomerId(Integer customerId) {
        return orderRepository.findOrderDTOsByCustomerId(customerId);
    }

    @Override
    public OrderDetailDTO findOrderDetailByOrderNumber(Integer customerId, String orderNumber) {
        Order order = orderRepository.findOrderDetail(orderNumber).orElseThrow(
                () -> new NotFoundException("Order Not Found")
        );

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new AccessDeniedException("You dont have permission to view this content");
        }

        List<OrderItemDTO> orderItems = orderItemRepository.findFullByOrderId(order.getId());

        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        orderDetailDTO.setShippingOrderCode(order.getShippingOrderCode());
        orderDetailDTO.setExpectedDeliveryTime(order.getExpectedDeliveryTime());
        orderDetailDTO.setOrderNumber(order.getOrderNumber());
        orderDetailDTO.setCreatedAt(order.getCreatedAt());
        orderDetailDTO.setShippingAmount(order.getShippingAmount());
        orderDetailDTO.setShippingAddress(order.getShippingAddress().toString());
        orderDetailDTO.setTotalAmount(order.getTotalAmount());
        orderDetailDTO.setDiscountAmount(order.getDiscountAmount());
        orderDetailDTO.setFinalAmount(order.getFinalAmount());
        orderDetailDTO.setShippingStatus(order.getShippingStatus().name());
        orderDetailDTO.setOrderStatus(order.getStatus().name());
        orderDetailDTO.setPaymentStatus(order.getPaymentStatus().name());
        orderDetailDTO.setItems(orderItems);

        PaymentDTO paymentDTO = order.getPayment() != null ? new PaymentDTO(
                order.getPayment().getPaymentMethod(),
                order.getPayment().getAmount(),
                order.getPayment().getPaymentDate(),
                order.getPayment().getStatus().name(),
                order.getPayment().getTransactionId()
        ) : null;

        List<OrderHistoryDTO> historyDTOs = order.getStatusHistory().stream()
                .map(history -> new OrderHistoryDTO(
                        history.getId(),
                        history.getStatus(),
                        history.getChangedAt()
                ))
                .sorted(Comparator.comparing(OrderHistoryDTO::getChangedAt))
                .toList();

        orderDetailDTO.setPayment(paymentDTO);
        orderDetailDTO.setHistory(historyDTOs);

        return orderDetailDTO;
    }


    private Customer validateCustomer(Integer customerId) {
        return customerRepository.findByIdWithAddress(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));
    }

    private Address validateShippingAddress(Customer customer, Integer shippingAddressId) {
        return customer.getAddresses().stream()
                .filter(address -> address.getId().equals(shippingAddressId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
    }

    private ShoppingCart validateCart(Integer customerId) {
        ShoppingCart cart = cartService.getOrCreateCartForCustomer(customerId);
        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
        return cart;
    }

    private Order buildOrder(Customer customer, Address shippingAddress, ShoppingCart cart) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCustomer(customer);
        order.setShippingAddress(shippingAddress);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShippingStatus(ShippingStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal shippingAmount = BigDecimal.ZERO;

        for (CartItem cartItemRequest : cart.getCartItems()) {
            OrderItem orderItem = createOrderItem(order, cartItemRequest);
            order.getOrderItems().add(orderItem);
            BigDecimal itemTotal = orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            BigDecimal itemDiscount = itemTotal.multiply(orderItem.getDiscountPercent()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            discountAmount = discountAmount.add(itemDiscount);
        }

        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);

        // shipping will be calculated after admin confirm the order.
        order.setShippingAmount(shippingAmount);
        order.setFinalAmount(totalAmount.subtract(discountAmount).add(shippingAmount));

        return order;
    }


    private OrderItem createOrderItem(Order order, CartItem cartItem) {
        Sku sku = cartItem.getSku();
        int quantity = cartItem.getQuantity();

        BigDecimal price = sku.getPrice();
        BigDecimal discount = sku.getDiscountedPrice();
        BigDecimal itemTotal = discount.multiply(BigDecimal.valueOf(quantity));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setSku(sku);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price);
        orderItem.setDiscountPercent(sku.getDiscountPercent());
        orderItem.setTotalAmount(itemTotal);
        return orderItem;
    }

//    @Scheduled(fixedRate = 300000)
//    @Transactional
//    public void releaseExpiredReservations() {
//        List<Order> expiredOrders = orderRepository.findExpiredOrders(LocalDateTime.now());
//
//        for (Order expiredOrder : expiredOrders) {
//            releaseStock(expiredOrder);
//            expiredOrder.setStatus(OrderStatus.EXPIRED.name());
//            orderRepository.save(expiredOrder);
//        }
//    }

    private void releaseStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Sku sku = orderItem.getSku();
            sku.setStockQuantity(sku.getStockQuantity() + orderItem.getQuantity());
            skuRepository.save(sku);
        }
    }
}
