package com.tranhuy105.site.service;

public interface NotificationService {
    void sendNotification(String customerId, String message);

    void sendNotification(String customerId, String message, String href);
}
