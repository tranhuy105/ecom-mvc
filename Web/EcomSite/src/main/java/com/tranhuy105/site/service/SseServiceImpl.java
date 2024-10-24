package com.tranhuy105.site.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class SseServiceImpl implements SseService {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter createConnection(String customerId) {
        SseEmitter oldEmitter = emitters.get(customerId);
        if (oldEmitter != null) {
            log.debug("Closing existing connection for customer {}", customerId);
            oldEmitter.complete();
        }

        log.debug("New connection for customer {}",customerId);
        SseEmitter emitter = new SseEmitter(90000L);
        emitters.put(customerId, emitter);
        emitter.onCompletion(() -> emitters.remove(customerId));
        emitter.onTimeout(() -> {
            log.debug("Connection timed out for customer {}", customerId);
            emitters.remove(customerId);
            emitter.complete();
        });
        emitter.onError((throwable) -> {
            log.debug("Connection error for customer {}: {}", customerId, throwable.getMessage());
            emitters.remove(customerId);
            emitter.complete();
        });
        return emitter;
    }

    @Override
    public void sendEvent(String customerId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(customerId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
                log.debug("Sent SSE event {}", eventName);
            } catch (IllegalStateException e) {
                log.warn("Attempted to send event '{}' to closed connection for customer {}: {}", eventName, customerId, e.getMessage());
                emitter.completeWithError(e);
            } catch (Exception e) {
                log.debug("Client {} disconnected while sending {} event: {}", customerId, eventName, e.getMessage());
                emitter.completeWithError(e);
            }
        } else {
            log.debug("No active connection for customer: " + customerId);
        }
    }

    @Override
    public void closeConnection(String customerId) {
        SseEmitter emitter = emitters.get(customerId);
        if (emitter != null) {
            emitter.complete();
            emitters.remove(customerId);
        }
    }

    @EventListener(ContextClosedEvent.class)
    public void shutdown() {
        log.info("Shutting down and closing all active SSE connections...");
        emitters.forEach((customerId, emitter) -> {
            CompletableFuture.runAsync(() -> {
                        try {
                            emitter.complete();
                            log.info("Closed SSE connection for customer {}", customerId);
                        } catch (Exception e) {
                            log.warn("Error while closing SSE connection for customer {}: {}", customerId, e.getMessage());
                        }
                    }).orTimeout(5, TimeUnit.SECONDS)
                    .exceptionally(ex -> {
                        log.warn("Failed to close SSE connection for customer {}: {}", customerId, ex.getMessage());
                        return null;
                    });
        });
        emitters.clear();
    }
}
