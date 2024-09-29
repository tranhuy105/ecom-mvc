package com.tranhuy105.site.dto;

import java.time.LocalDateTime;

public record RedirectNotification(String message, LocalDateTime timestamp, String href, Integer Id) {
}
