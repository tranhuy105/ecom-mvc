package com.tranhuy105.site.payment;

import com.tranhuy105.common.constant.PaymentMethod;
import com.tranhuy105.common.entity.Order;
import com.tranhuy105.site.exception.PaymentException;
import com.tranhuy105.site.dto.PaymentGatewayResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface PaymentService {
    @Transactional
    String initiatePayment(Order order, PaymentMethod paymentMethod, HttpServletRequest request) throws PaymentException;

    @Transactional
    PaymentGatewayResponse handlePaymentCallback(Map<String, String> params, PaymentMethod paymentMethod);
}
