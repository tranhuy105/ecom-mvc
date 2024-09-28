package com.tranhuy105.site.payment;

import com.tranhuy105.common.constant.OrderStatus;
import com.tranhuy105.common.constant.PaymentMethod;
import com.tranhuy105.common.entity.*;
import com.tranhuy105.site.dto.*;
import com.tranhuy105.site.event.OrderCancelledEvent;
import com.tranhuy105.site.event.OrderCreatedEvent;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.repository.OrderItemRepository;
import com.tranhuy105.site.repository.OrderRepository;
import com.tranhuy105.site.repository.CustomerRepository;
import com.tranhuy105.site.service.ShoppingCartService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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
    private final OrderItemRepository orderItemRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ShoppingCartService shoppingCartService;


    @Transactional
    @Override
    public Order createOrder(Integer customerId, Integer shippingAddressId, PaymentMethod paymentMethod, HttpServletRequest request) {
        Customer customer = validateCustomer(customerId);
        String customerIp = request.getRemoteAddr();
        Address shippingAddress = validateShippingAddress(customer, shippingAddressId);
        ShoppingCart cart = validateCart(customerId);
        Order order = buildOrder(customer, shippingAddress, cart);
        order.setReservationExpiry(LocalDateTime.now().plusMinutes(15));
        order = updateOrderStatus(order, OrderStatus.PENDING);
        eventPublisher.publishEvent(new OrderCreatedEvent(order, paymentMethod, customerIp));
        return order;
    }

    @Override
    public void cancelOrder(Integer customerId, String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new AccessDeniedException("You dont have the permission to perform this action");
        }

        if (order.getStatus().equals(OrderStatus.PENDING)
                || order.getStatus().equals(OrderStatus.CONFIRMED)) {
            order = updateOrderStatus(order, OrderStatus.CANCELED);
            eventPublisher.publishEvent(new OrderCancelledEvent(order));
        } else {
            throw new IllegalArgumentException("You can not cancel order at this stage");
        }
    }

    @Override
    public Order updateOrderStatus(Order order, OrderStatus newStatus) {
        boolean statusChanged = !newStatus.equals(order.getStatus());

        order.setStatus(newStatus);

        if (statusChanged) {
            OrderStatusHistory history = new OrderStatusHistory();
            history.setOrder(order);
            history.setStatus(newStatus.name());
            order.getStatusHistory().add(history);
        }

        return orderRepository.save(order);
    }


    @Transactional
    @Override
    public void confirmOrder(Order order) {
        updateOrderStatus(order, OrderStatus.CONFIRMED);
        shoppingCartService.clearShoppingCart(order.getCustomer().getId());
    }

    @Transactional
    @Override
    public void cancelOrder(Order order) {
        order = updateOrderStatus(order, OrderStatus.CANCELED);
        eventPublisher.publishEvent(new OrderCancelledEvent(order));
    }

    @Override
    public Order findOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumberWithPayment(orderNumber).orElse(null);
    }

    @Override
    public List<OrderDTO> findOrderByCustomerId(Integer customerId) {
        // TODO: make the pagination and sort & filterable
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

        List<OrderItemDTO> orderItems = orderItemRepository.findFullByOrderId(order.getId(), customerId);

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
        orderDetailDTO.setOrderStatus(order.getStatus().name());
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




    // --- PRIVATE HELPER METHOD ----
    private Customer validateCustomer(Integer customerId) {
        Customer customer = customerRepository.findByIdWithAddress(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));

        if (customer.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Customer Must Registered A Phone Number Before Placing Orders.");
        }

        return customer;
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
}
