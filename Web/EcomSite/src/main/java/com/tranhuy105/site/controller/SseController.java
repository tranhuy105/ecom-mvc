package com.tranhuy105.site.controller;

import com.tranhuy105.common.entity.Customer;
import com.tranhuy105.site.service.CustomerService;
import com.tranhuy105.site.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseController {
    private final SseService sseService;
    private final CustomerService customerService;

    public SseController(SseService sseService, CustomerService customerService) {
        this.sseService = sseService;
        this.customerService = customerService;
    }

    // Connect to the SSE stream
    @GetMapping(path = "/sse/connect", produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(Authentication authentication) {
        Customer customer = customerService.getCustomerFromAuthentication(authentication);
        if (customer == null) {
            throw new IllegalStateException("User not authenticated");
        }
        return sseService.createConnection(customer.getId().toString());
    }
}
