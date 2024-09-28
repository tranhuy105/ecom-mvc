package com.tranhuy105.site.payment;

import com.tranhuy105.common.constant.PaymentMethod;
import com.tranhuy105.common.constant.PaymentStatus;
import com.tranhuy105.common.entity.Order;
import com.tranhuy105.common.entity.Payment;
import com.tranhuy105.site.dto.PaymentGatewayResponse;
import com.tranhuy105.site.exception.OrderNotFoundException;
import com.tranhuy105.site.exception.PaymentAlreadyProcessedException;
import com.tranhuy105.site.exception.PaymentException;
import com.tranhuy105.site.exception.PaymentNotFoundException;
import com.tranhuy105.site.payment.client.PaymentGatewayClient;
import com.tranhuy105.site.repository.PaymentRepository;
import com.tranhuy105.site.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final Map<String, PaymentGatewayClient> paymentGatewayClients;
    private final OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final SseService sseService;

    @Override
    @Transactional
    public String initiatePayment(Order order, PaymentMethod paymentMethod, String userIP) throws PaymentException {
        PaymentGatewayClient paymentClient = getPaymentClient(paymentMethod);
        String paymentUrl = paymentClient.createPaymentURL(order, userIP);

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getFinalAmount())
                .paymentMethod(paymentMethod.toString())
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        return paymentUrl;
    }

    @Override
    @Transactional
    public PaymentGatewayResponse handlePaymentCallback(Map<String, String> params, PaymentMethod paymentMethod) {
        PaymentGatewayClient paymentClient = getPaymentClient(paymentMethod);
        PaymentGatewayResponse response = paymentClient.parseCallback(params);

        try {
            processPayment(paymentClient, response);
            return response;
        } catch (OrderNotFoundException e) {
            log.warn("Order not found while processing payment for order {}: {}", response.getOrderNumber(), e.getMessage());
            response.setSuccess(false);
            response.setStatusMessage("Order not found");
            return response;
        } catch (PaymentNotFoundException e) {
            log.warn("Payment not found for order {}: {}", response.getOrderNumber(), e.getMessage());
            response.setSuccess(false);
            response.setStatusMessage("Payment not found");
            return response;

        } catch (PaymentAlreadyProcessedException e) {
            log.warn("Payment already processed for order {}: {}", response.getOrderNumber(), e.getMessage());
            response.setSuccess(false);
            response.setStatusMessage("Payment already processed");
            return response;
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing payment for order {}: {}", response.getOrderNumber(), e.getMessage(), e);
            throw new PaymentException("An unexpected error occurred");
        }
    }


    @Override
    public PaymentGatewayResponse parsePaymentResponse(Map<String, String> params, PaymentMethod paymentMethod) {
        PaymentGatewayClient paymentClient = getPaymentClient(paymentMethod);
        return paymentClient.parseCallback(params);
    }

    private PaymentGatewayClient getPaymentClient(PaymentMethod method) {
        PaymentGatewayClient paymentClient = paymentGatewayClients.get(method.toString());
        if (paymentClient != null) {
            return paymentClient;
        }
        throw new PaymentException("Unsupported payment method: " + method);
    }

    private void processPayment(PaymentGatewayClient paymentClient, PaymentGatewayResponse response) throws OptimisticLockingFailureException {
        Order order = orderService.findOrderByOrderNumber(response.getOrderNumber());
        if (order == null) {
            throw new OrderNotFoundException(response.getOrderNumber());
        }

        Payment payment = order.getPayment();
        if (payment == null) {
            throw new PaymentNotFoundException(order.getOrderNumber());
        }

        if (!payment.getStatus().equals(PaymentStatus.PENDING)) {
            log.warn("Payment for order {} has already been processed with status {}.", order.getOrderNumber(), payment.getStatus());
            throw new PaymentAlreadyProcessedException(order.getOrderNumber());
        }

        boolean isSuccessful = paymentClient.isPaymentSuccessful(response.getStatusCode());
        payment.setTransactionId(response.getTransactionId());

        if (isSuccessful) {
            handlePaymentSuccess(order);
            response.setSuccess(true);
        } else {
            handlePaymentFailure(order);
            response.setSuccess(false);
        }

        sseService.sendEvent(order.getCustomer().getId().toString(), "payment-complete", order.getOrderNumber());
    }

    private void handlePaymentSuccess(Order order) {
        Payment payment = order.getPayment();
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.saveAndFlush(payment);

        orderService.confirmOrder(order);
        log.info("Payment for order {} was successful. Transaction ID: {}", order.getOrderNumber(), payment.getTransactionId());

    }

    private void handlePaymentFailure(Order order) {
        Payment payment = order.getPayment();
        payment.setStatus(PaymentStatus.FAILED);
        payment.setTransactionId(null);
        paymentRepository.saveAndFlush(payment);

        orderService.cancelOrder(order);
        log.warn("Payment for order {} failed. Payment marked as FAILED.", order.getOrderNumber());
    }
}
