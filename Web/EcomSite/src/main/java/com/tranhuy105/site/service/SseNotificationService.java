package com.tranhuy105.site.service;

import com.tranhuy105.site.dto.Notification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SseNotificationService implements NotificationService {

    private final SseService sseService;

    public SseNotificationService(SseService sseService) {
        this.sseService = sseService;
    }

    @Override
    public void sendNotification(String customerId, String message) {
        sseService.sendEvent(customerId, "notification", new Notification(message, LocalDateTime.now(), "#"));
    }

    @Override
    public void sendNotification(String customerId, String message, String href) {
        sseService.sendEvent(customerId, "notification", new Notification(message, LocalDateTime.now(), href));
    }


//    @Scheduled(fixedRate = 10000L)
//    public void test() {
//        sendNotification("2", "Tan chảy vì đường! Thiên sứ tập mới nhất!", "/site/products/manga-thien-su-nha-ben-tap-2-tang-kem-bookmark-be-hinh-postcard-in-nhu-2-mat");
//    }
}
