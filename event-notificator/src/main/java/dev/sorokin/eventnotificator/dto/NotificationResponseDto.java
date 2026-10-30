package dev.sorokin.eventnotificator.dto;

import java.util.List;
import java.util.UUID;

public record NotificationResponseDto(
        Long notificationId,
        String type,
        Long eventId,
        String createdAt,
        boolean isRead,
        NotificationPayload payload
) {

    public record NotificationPayload(
            UUID messageId,
            String eventType,
            String occurredAt,
            Long changedById,
            Long ownerId,
            List<NotificationChange> changes
    ) {

        public record NotificationChange(
                String field,
                String oldValue,
                String newValue
        ) {}
    }
}