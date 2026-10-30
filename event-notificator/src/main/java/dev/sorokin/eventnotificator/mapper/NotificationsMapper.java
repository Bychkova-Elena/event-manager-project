package dev.sorokin.eventnotificator.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventnotificator.dto.NotificationResponseDto;
import dev.sorokin.eventnotificator.entity.NotificationEntity;
import dev.sorokin.eventnotificator.entity.NotificationPayloadsEntity;
import dev.sorokin.eventnotificator.model.Notification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationsMapper {

    private final ObjectMapper objectMapper;

    public NotificationsMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Notification mapToNotificationModel(
            NotificationEntity entity,
            NotificationPayloadsEntity payloads
    ) throws JsonProcessingException {

        List<Notification.NotificationPayload.NotificationChange> changes = extractChanges(payloads);

        Notification.NotificationPayload payload = Notification.NotificationPayload.builder()
                .messageId(payloads.getMessageId())
                .eventType(payloads.getEventType())
                .occurredAt(payloads.getOccurredAt())
                .changedById(payloads.getChangedById())
                .ownerId(payloads.getOwnerId())
                .changes(changes)
                .build();

        return Notification.builder()
                .notificationId(entity.getNotificationId())
                .type(payload.eventType())
                .eventId(payloads.getEventId())
                .createdAt(entity.getCreatedAt().toString())
                .isRead(entity.getIsRead())
                .payload(payload)
                .build();
    }

    private List<Notification.NotificationPayload.NotificationChange> extractChanges(
            NotificationPayloadsEntity payloads
    ) throws JsonProcessingException {
        Object changesRaw = payloads.getPayload().get("changes");

        if (changesRaw == null) {
            return List.of();
        }

        if (changesRaw instanceof String s) {
            return objectMapper.readValue(
                    s,
                    new TypeReference<List<Notification.NotificationPayload.NotificationChange>>() {}
            );
        }
        return objectMapper.convertValue(
                changesRaw,
                new TypeReference<List<Notification.NotificationPayload.NotificationChange>>() {}
        );
    }

    public List<NotificationResponseDto> toDtoList(List<Notification> notifications) {
        List<NotificationResponseDto> dtos = new ArrayList<>();

        for (Notification notification : notifications) {
            dtos.add(toDto(notification));
        }

        return dtos;
    }

    private NotificationResponseDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        Notification.NotificationPayload p = notification.payload();

        return new NotificationResponseDto(
                notification.notificationId(),
                notification.type(),
                notification.eventId(),
                notification.createdAt(),
                notification.isRead(),
                p == null ? null : toPayloadDto(p)
        );
    }

    private NotificationResponseDto.NotificationPayload toPayloadDto(
            Notification.NotificationPayload payload
    ) {
        return new NotificationResponseDto.NotificationPayload(
                payload.messageId(),
                payload.eventType(),
                payload.occurredAt() == null ? null : payload.occurredAt().toString(),
                payload.changedById(),
                payload.ownerId(),
                toChangeDtoList(payload.changes())
        );
    }

    private List<NotificationResponseDto.NotificationPayload.NotificationChange> toChangeDtoList(
            List<Notification.NotificationPayload.NotificationChange> changes
    ) {
        if (changes == null) {
            return List.of();
        }
        return changes.stream()
                .map(c -> new NotificationResponseDto.NotificationPayload.NotificationChange(
                        c.field(),
                        c.oldValue(),
                        c.newValue()
                ))
                .toList();
    }
}
