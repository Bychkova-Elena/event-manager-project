package dev.sorokin.eventnotificator.exception.dto;

import java.time.LocalDateTime;

public record ErrorMessageResponse(
        String message,
        String detailMessage,
        LocalDateTime dateTime
) {
}
