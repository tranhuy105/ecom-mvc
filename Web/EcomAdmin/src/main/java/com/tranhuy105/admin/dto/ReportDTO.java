package com.tranhuy105.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReportDTO {
    private String identifier;
    private BigDecimal grossSales;
    private BigDecimal netSales;
    private int ordersCount;
}
