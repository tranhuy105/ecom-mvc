package com.tranhuy105.site.dto;

import lombok.Data;

@Data
public class PaymentGatewayResponse {
    private String transactionId;
    private String statusCode;
    private String statusMessage;
    private String paymentMethod;
    private String orderNumber;
    private String gatewayResponse;
    private Integer amount;
    private boolean success;
}
