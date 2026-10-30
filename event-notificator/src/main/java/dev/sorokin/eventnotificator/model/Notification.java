package dev.sorokin.eventnotificator.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Notification(
        Long notificationId,
        String type,
        Long eventId,
        String createdAt,
        boolean isRead,
        NotificationPayload payload
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long notificationId;
        private String type;
        private Long eventId;
        private String createdAt;
        private boolean isRead;
        private NotificationPayload payload;

        public Builder notificationId(Long notificationId) {
            this.notificationId = notificationId;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder eventId(Long eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder createdAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder isRead(boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public Builder payload(NotificationPayload payload) {
            this.payload = payload;
            return this;
        }

        public Notification build() {
            return new Notification(notificationId, type, eventId, createdAt, isRead, payload);
        }
    }

    public record NotificationPayload(
            UUID messageId,
            String eventType,
            Instant occurredAt,
            Long changedById,
            Long ownerId,
            List<NotificationChange> changes
    ) {

        public static PayloadBuilder builder() {
            return new PayloadBuilder();
        }

        public static class PayloadBuilder {
            private UUID messageId;
            private String eventType;
            private Instant occurredAt;
            private Long changedById;
            private Long ownerId;
            private List<NotificationChange> changes = new ArrayList<>();

            public PayloadBuilder messageId(UUID messageId) {
                this.messageId = messageId;
                return this;
            }

            public PayloadBuilder eventType(String eventType) {
                this.eventType = eventType;
                return this;
            }

            public PayloadBuilder occurredAt(Instant occurredAt) {
                this.occurredAt = occurredAt;
                return this;
            }

            public PayloadBuilder changedById(Long changedById) {
                this.changedById = changedById;
                return this;
            }

            public PayloadBuilder ownerId(Long ownerId) {
                this.ownerId = ownerId;
                return this;
            }

            public PayloadBuilder changes(List<NotificationChange> changes) {
                this.changes = changes != null ? changes : new ArrayList<>();
                return this;
            }

            public NotificationPayload build() {
                return new NotificationPayload(messageId, eventType, occurredAt, changedById, ownerId, changes);
            }
        }

        public record NotificationChange(
                String field,
                String oldValue,
                String newValue
        ) {
            public static ChangeBuilder builder() {
                return new ChangeBuilder();
            }

            public static class ChangeBuilder {
                private String field;
                private String oldValue;
                private String newValue;

                public ChangeBuilder field(String field) {
                    this.field = field;
                    return this;
                }

                public ChangeBuilder oldValue(String oldValue) {
                    this.oldValue = oldValue;
                    return this;
                }

                public ChangeBuilder newValue(String newValue) {
                    this.newValue = newValue;
                    return this;
                }

                public NotificationChange build() {
                    return new NotificationChange(field, oldValue, newValue);
                }
            }
        }
    }
}