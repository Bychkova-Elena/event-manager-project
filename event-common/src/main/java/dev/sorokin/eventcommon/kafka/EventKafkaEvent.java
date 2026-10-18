package dev.sorokin.eventcommon.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record EventKafkaEvent(
        UUID messageId,
        EventType eventType,
        Long eventId,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
        Instant occurredAt,
        Long ownerId,
        Long changedById,
        List<Long> subscribers,
        List<FieldChange> changes
) {
    public EventKafkaEvent {
        if (subscribers == null) subscribers = List.of();
        if (changes == null) changes = List.of();
    }

    public record FieldChange(
            String field,
            String oldValue,
            String newValue
    ) {
    }
}
