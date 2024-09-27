package com.tranhuy105.site.payment;

import com.tranhuy105.common.constant.OrderStatus;
import com.tranhuy105.common.constant.PaymentMethod;
import com.tranhuy105.common.constant.PaymentStatus;
import com.tranhuy105.common.entity.Order;
import com.tranhuy105.common.entity.OrderStatusHistory;
import com.tranhuy105.common.entity.Payment;
import com.tranhuy105.site.exception.PaymentException;
import com.tranhuy105.site.payment.client.PaymentGatewayClient;
import com.tranhuy105.site.dto.PaymentGatewayResponse;
import com.tranhuy105.site.repository.OrderRepository;
import com.tranhuy105.site.service.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final Map<String, PaymentGatewayClient> paymentGatewayClients;
    private final OrderRepository orderRepository;
    private final ShoppingCartService cartService;

    @Override
    @Transactional
    public String initiatePayment(Order order, PaymentMethod paymentMethod, HttpServletRequest request) throws PaymentException {
        PaymentGatewayClient paymentClient = getPaymentClient(paymentMethod);
        String url = paymentClient.createPaymentURL(order, request);

        order.setPaymentStatus(PaymentStatus.PENDING);
        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getFinalAmount())
                .paymentMethod(paymentMethod.toString())
                .status(PaymentStatus.PENDING)
                .build();
        order.setPayment(payment);
        orderRepository.saveAndFlush(order);
        return url;
    }

    @Override
    @Transactional
    public PaymentGatewayResponse handlePaymentCallback(Map<String, String> params, PaymentMethod paymentMethod) {
        PaymentGatewayClient paymentClient = getPaymentClient(paymentMethod);
        PaymentGatewayResponse response = paymentClient.parseCallback(params);

        Order order = orderRepository.findByOrderNumberWithPayment(response.getOrderNumber())
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));

        if (!order.getPaymentStatus().equals(PaymentStatus.PENDING)) {
            throw new IllegalArgumentException("This payment has already been processed or have been expired");
        }

        try {
            boolean isSuccessful = paymentClient.isPaymentSuccessful(response.getStatusCode());
            OrderStatusHistory history = new OrderStatusHistory();
            history.setOrder(order);
            if (isSuccessful) {
                updateOnSuccessPayment(order);
                cartService.clearShoppingCart(order.getCustomer().getId());
                history.setStatus("PAYMENT_ACCEPTED");
                response.setSuccess(true);
            } else {
                updateOnFailPayment(order);
                history.setStatus(OrderStatus.CANCELED.name());
                response.setSuccess(false);
            }

            order.getPayment().setTransactionId(response.getTransactionId());
            order.getStatusHistory().add(history);
        } catch (Exception e) {
            log.error("ERROR WHILE PROCESSING PAYMENT " +order.getPayment().getId(), e);
            updateOnFailPayment(order);
            response.setSuccess(false);
        } finally {
            orderRepository.saveAndFlush(order);
        }

        return response;
    }

    @Override
    public PaymentGatewayResponse parsePaymentResponse(Map<String, String> params, PaymentMethod paymentMethod) {
        PaymentGatewayClient paymentClient = getPaymentClient(paymentMethod);
        return paymentClient.parseCallback(params);
    }


    private void updateOnSuccessPayment(Order order) {
        order.setPaymentStatus(PaymentStatus.PAID);
        order.getPayment().setStatus(PaymentStatus.PAID);
        order.getPayment().setPaymentDate(LocalDateTime.now());
    }

    private void updateOnFailPayment(Order order) {
        order.getPayment().setStatus(PaymentStatus.FAILED);
        order.setPaymentStatus(PaymentStatus.FAILED);
        order.setStatus(OrderStatus.CANCELED);
    }

    private PaymentGatewayClient getPaymentClient(PaymentMethod method) {
        PaymentGatewayClient paymentClient = paymentGatewayClients.get(method.toString());
        if (paymentClient != null) {
            return paymentClient;
        }
        throw new PaymentException("Unsupported payment method");
    }
}
