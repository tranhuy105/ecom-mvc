package com.tranhuy105.site.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    SseEmitter createConnection(String customerId);

    void sendEvent(String customerId, String eventName, Object data);

    void closeConnection(String customerId);
}

