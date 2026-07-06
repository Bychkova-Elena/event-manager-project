package dev.sorokin.eventmanager.exception.dto;

import java.time.LocalDateTime;

public record ErrorMessageResponse(
        String message,
        String detailMessage,
        LocalDateTime dateTime
) {
}
