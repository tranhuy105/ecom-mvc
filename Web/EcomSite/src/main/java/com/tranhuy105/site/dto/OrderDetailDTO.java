package com.tranhuy105.site.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderDetailDTO {
    private String orderNumber;
    private LocalDateTime createdAt;
    private String shippingAddress;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private BigDecimal shippingAmount;
    private String shippingStatus;
    private String orderStatus;
    private String paymentStatus;
    private List<OrderItemDTO> items;
    private List<OrderHistoryDTO> history;
    private PaymentDTO payment;

}
