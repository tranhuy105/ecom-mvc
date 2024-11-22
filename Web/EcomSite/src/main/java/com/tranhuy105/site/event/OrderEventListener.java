package com.tranhuy105.site.event;

import com.tranhuy105.common.constant.OrderStatus;
import com.tranhuy105.common.constant.PaymentMethod;
import com.tranhuy105.common.entity.Order;
import com.tranhuy105.site.exception.StockUnavailableException;
import com.tranhuy105.site.payment.OrderService;
import com.tranhuy105.site.payment.PaymentService;
import com.tranhuy105.site.payment.StockService;
import com.tranhuy105.site.service.SchedulerService;
import com.tranhuy105.site.service.ShoppingCartService;
import com.tranhuy105.site.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderEventListener {
    private final StockService stockService;
    private final OrderService orderService;
    private final ShoppingCartService shoppingCartService;
    private final SseService sseService;
    private final SchedulerService schedulerService;

    @EventListener
    public void handleOrderCancelled(OrderCancelledEvent event) {
        Order order = event.order();
        stockService.releaseStock(order);
        log.info("Stock released for cancelled order {}", order.getOrderNumber());
    }

    @EventListener
    @Async
    public void handleOrderCancelledSchedule(OrderCancelledEvent event) {
        Order order = event.order();
        schedulerService.cancelPaymentExpiryJob(order.getId());
        schedulerService.cancelOrderExpirationJob(order.getOrderNumber());
    }

    @Async
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        Order order = event.order();
        PaymentMethod paymentMethod = event.paymentMethod();

        try {
            stockService.reserveStock(order);
            if (paymentMethod.equals(PaymentMethod.COD)) {
                shoppingCartService.clearShoppingCart(order.getCustomer().getId());
            } else {
                schedulerService.scheduleOrderExpiration(order);
            }
        } catch (StockUnavailableException e) {
            log.error("Failed to reserve stock for order {}", order.getOrderNumber(), e);
            orderService.updateOrderStatus(order, OrderStatus.CANCELED);
        }
    }
}
