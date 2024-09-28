package com.tranhuy105.site.event;

import com.tranhuy105.common.constant.OrderStatus;
import com.tranhuy105.common.constant.PaymentMethod;
import com.tranhuy105.common.constant.PaymentStatus;
import com.tranhuy105.common.entity.Order;
import com.tranhuy105.common.entity.Payment;
import com.tranhuy105.site.exception.StockUnavailableException;
import com.tranhuy105.site.payment.OrderService;
import com.tranhuy105.site.payment.PaymentService;
import com.tranhuy105.site.payment.StockService;
import com.tranhuy105.site.service.ShoppingCartService;
import com.tranhuy105.site.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderEventListener {
    private final StockService stockService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ShoppingCartService shoppingCartService;
    private final SseService sseService;

    @EventListener
    @Async
    public void handleOrderCancelled(OrderCancelledEvent event) {
        Order order = event.order();
        stockService.releaseStock(order);
        log.info("Stock released for cancelled order {}", order.getOrderNumber());
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
                String paymentUrl = paymentService.initiatePayment(order, paymentMethod, event.userIp());
                // TODO: FIRE AN SSE
                log.info(order.getCustomer().getId().toString());
                log.info(paymentUrl);
                log.info("about to send an event");

                Thread.sleep(3000);
                log.info("SENT EVENT");
                sseService.sendEvent(order.getCustomer().getId().toString(), "payment-initiated-ok", paymentUrl);
            }
        } catch (StockUnavailableException e) {
            log.error("Failed to reserve stock for order {}", order.getOrderNumber(), e);
            orderService.updateOrderStatus(order, OrderStatus.CANCELED);
        } catch (InterruptedException e) {
            log.error("Thread interrupted while waiting to send SSE event", e);
        }
    }
}
