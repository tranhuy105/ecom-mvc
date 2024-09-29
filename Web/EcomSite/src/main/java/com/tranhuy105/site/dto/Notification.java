package com.tranhuy105.site.dto;

import java.time.LocalDateTime;

public record Notification(String message, LocalDateTime timestamp, String href) {
}
