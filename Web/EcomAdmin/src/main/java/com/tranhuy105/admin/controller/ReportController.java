package com.tranhuy105.admin.controller;

import com.tranhuy105.admin.dto.ReportDTO;
import com.tranhuy105.admin.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {
    private final OrderService orderService;

    @GetMapping
    public String viewSaleReportHome() {
        return "reports/report";
    }

    @ResponseBody
    @GetMapping("last_7_days")
    ResponseEntity<List<ReportDTO>> findReportLast7days() {
        return ResponseEntity.ok(orderService.getOrderReportLastXDays(7));
    }

    @ResponseBody
    @GetMapping("last_28_days")
    ResponseEntity<List<ReportDTO>> findReportLast28days() {
        return ResponseEntity.ok(orderService.getOrderReportLastXDays(28));
    }

    @ResponseBody
    @GetMapping("last_6_months")
    ResponseEntity<List<ReportDTO>> findReportLast6Months() {
        return ResponseEntity.ok(orderService.getOrderReportLastXDays(180));
    }

    @ResponseBody
    @GetMapping("last_year")
    ResponseEntity<List<ReportDTO>> findReportLastYear() {
        return ResponseEntity.ok(orderService.getOrderReportLastXDays(365));
    }

    @ResponseBody
    @GetMapping("/{startDate}/{endDate}")
    ResponseEntity<List<ReportDTO>> findReportByCustomRange(@PathVariable("startDate") String startDateStr,
                                                            @PathVariable("endDate") String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        return ResponseEntity.ok(orderService.getOrderReportByRange(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)));
    }
}
