package com.tranhuy105.admin.controller;

import com.tranhuy105.admin.dto.OrderItemDTO;
import com.tranhuy105.admin.dto.OrderOverviewDTO;
import com.tranhuy105.admin.dto.ghn.GhnDistrictData;
import com.tranhuy105.admin.dto.ghn.GhnOrderResponse;
import com.tranhuy105.admin.dto.ghn.GhnProvinceData;
import com.tranhuy105.admin.dto.ghn.GhnWardData;
import com.tranhuy105.admin.service.GhnApiService;
import com.tranhuy105.admin.service.OrderService;
import com.tranhuy105.common.entity.Order;
import com.tranhuy105.common.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final GhnApiService ghnApiService;

    @ResponseBody
    @GetMapping("/orders/{id}/items")
    public ResponseEntity<List<OrderItemDTO>> fetchOrderItems(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrderItems(id));
    }

    @GetMapping("/orders/{orderNumber}/detail")
    public String viewOrderDetail(@PathVariable String orderNumber,
                                  Model model) {
        Order order = orderService.getOrderDetails(orderNumber);
        if (order == null) {
            return "error/404";
        }

        model.addAttribute("order", order);
        return "orders/detail";
    }


    @GetMapping("/orders")
    public String listOrders(
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) String shippingStatus,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            Model model) {
        Page<OrderOverviewDTO> orderPage = orderService.listOrders(orderStatus, shippingStatus, paymentStatus,
                convertToLocalDateTimeViaInstant(startDate), convertToLocalDateTimeViaInstant(endDate),
                search, PageRequest.of(page - 1, 24, Sort.by("createdAt").descending()));
        PaginationUtil.setPaginationAttributes(page, 24, search, model, orderPage);
        model.addAttribute("orders", orderPage.getContent());
//        model.addAttribute("orders", new ArrayList<>());
        model.addAttribute("orderStatus", orderStatus);
        model.addAttribute("shippingStatus", shippingStatus);
        model.addAttribute("paymentStatus", paymentStatus);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "orders/listing";
    }

    private LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }

        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @ResponseBody
    @GetMapping("/orders/ghn/province")
    public ResponseEntity<List<GhnProvinceData>> getGhnProvince() {
        try {
            return ResponseEntity.ok(ghnApiService.getProvinces().getData());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ResponseBody
    @GetMapping("/orders/ghn/district")
    public ResponseEntity<List<GhnDistrictData>> getGhnDistrict(@RequestParam Integer provinceId) {
        try {
            return ResponseEntity.ok(ghnApiService.getDistricts(provinceId).getData());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ResponseBody
    @GetMapping("/orders/ghn/ward")
    public ResponseEntity<List<GhnWardData>> getGhnWard(@RequestParam Integer districtId) {
        try {
            return ResponseEntity.ok(ghnApiService.getWards(districtId).getData());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/orders/{orderId}/prepare")
    @ResponseBody
    public ResponseEntity<GhnOrderResponse.Data> prepareOrder(@PathVariable Integer orderId,
                                                         @RequestBody Map<String, Object> param) {
        try {
            return ResponseEntity.ok(orderService.prepareOrder(orderId, (String) param.get("WardCode"), Integer.valueOf((String) param.get("DistrictID"))));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/orders/{orderId}/confirm")
    @ResponseBody
    public ResponseEntity<String> confirmOrder(@PathVariable Integer orderId) {
        try {
            orderService.confirmOrder(orderId);
            return ResponseEntity.ok("SUCCESS");
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
        catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/orders/{orderId}/ship")
    @ResponseBody
    public ResponseEntity<String> shipOrder(@PathVariable Integer orderId) {
        try {
            orderService.shipOrder(orderId);
            return ResponseEntity.ok("SUCCESS");
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
        catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/orders/{orderId}/deliver")
    @ResponseBody
    public ResponseEntity<String> deliverOrder(@PathVariable Integer orderId) {
        try {
            orderService.deliverOrder(orderId);
            return ResponseEntity.ok("SUCCESS");
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
        catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/orders/{orderId}/print")
    @ResponseBody
    public ResponseEntity<String> printOrder(@PathVariable Integer orderId) {
        try {
            return ResponseEntity.ok(orderService.printA5ShippingLabel(orderId));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
        catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            return ResponseEntity.internalServerError().build();
        }
    }
}
