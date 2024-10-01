package com.tranhuy105.site.payment;

import com.tranhuy105.common.constant.OrderStatus;
import com.tranhuy105.common.constant.PaymentMethod;
import com.tranhuy105.common.entity.*;
import com.tranhuy105.site.dto.*;
import com.tranhuy105.site.event.OrderCancelledEvent;
import com.tranhuy105.site.event.OrderCreatedEvent;
import com.tranhuy105.site.exception.NotFoundException;
import com.tranhuy105.site.exception.OrderNotFoundException;
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
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));

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

            return orderRepository.save(order);
        }

        return order;
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
        Order order = validateAndFindOrder(customerId, orderNumber);
        List<OrderItemDTO> orderItems = findOrderItems(order, customerId);
        PaymentDTO paymentDTO = mapPaymentInfo(order);
        List<OrderHistoryDTO> historyDTOs = mapOrderHistory(order);

        return mapToOrderDetailDTO(order, orderItems, paymentDTO, historyDTOs);
    }

    @Override
    @Transactional
    public void expireOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));

        if (order.getStatus() == OrderStatus.PENDING) {
            order = updateOrderStatus(order, OrderStatus.EXPIRED);
            eventPublisher.publishEvent(new OrderCancelledEvent(order));
            log.info("Order {} has been expired due to timeout.", orderNumber);
        }
    }


    // --- PRIVATE HELPER METHOD ----
    private Customer validateCustomer(Integer customerId) {
        Customer customer = customerRepository.findByIdWithAddress(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));

        if (customer.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Customer Must Registered A Phone Number Before Placing Orders.");
        }

        if (customer.getAddresses().isEmpty()) {
            throw new IllegalArgumentException("Customer must have at least one address line to place an order");
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

    private Order validateAndFindOrder(Integer customerId, String orderNumber) {
        Order order = orderRepository.findOrderDetail(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new AccessDeniedException("You don't have permission to view this content");
        }

        return order;
    }

    private List<OrderItemDTO> findOrderItems(Order order, Integer customerId) {
        return orderItemRepository.findFullByOrderId(order.getId(), customerId);
    }

    private PaymentDTO mapPaymentInfo(Order order) {
        Payment payment = order.getPayment();

        return payment != null ? new PaymentDTO(
                payment.getPaymentMethod(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getStatus().name(),
                payment.getTransactionId()
        ) : null;
    }

    private List<OrderHistoryDTO> mapOrderHistory(Order order) {
        return order.getStatusHistory().stream()
                .map(history -> new OrderHistoryDTO(
                        history.getId(),
                        history.getStatus(),
                        history.getChangedAt()
                ))
                .sorted(Comparator.comparing(OrderHistoryDTO::getChangedAt))
                .toList();
    }

    private OrderDetailDTO mapToOrderDetailDTO(Order order, List<OrderItemDTO> orderItems,
                                               PaymentDTO paymentDTO, List<OrderHistoryDTO> historyDTOs) {
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
        orderDetailDTO.setPayment(paymentDTO);
        orderDetailDTO.setHistory(historyDTOs);

        return orderDetailDTO;
    }
}
