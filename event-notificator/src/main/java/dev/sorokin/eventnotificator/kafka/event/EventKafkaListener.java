package dev.sorokin.eventnotificator.kafka.event;

import dev.sorokin.eventcommon.kafka.EventKafkaEvent;
import dev.sorokin.eventnotificator.entity.NotificationEntity;
import dev.sorokin.eventnotificator.entity.NotificationPayloadsEntity;
import dev.sorokin.eventnotificator.repository.NotificationPayloadsRepository;
import dev.sorokin.eventnotificator.repository.NotificationsRepository;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class EventKafkaListener {

    private final NotificationPayloadsRepository notificationPayloadsRepository;
    private final NotificationsRepository notificationsRepository;

    private static final Logger log = LoggerFactory.getLogger(EventKafkaListener.class);

    public EventKafkaListener(
            NotificationPayloadsRepository notificationPayloadsRepository,
            NotificationsRepository notificationsRepository
    ) {
        this.notificationPayloadsRepository = notificationPayloadsRepository;
        this.notificationsRepository = notificationsRepository;
    }

    @KafkaListener(topics = "event-topic", containerFactory = "containerFactory")
    @Transactional
    public void listenEvents(
            ConsumerRecord<UUID, EventKafkaEvent> record
    ) {
        log.info("get event: event={}", record.value());
        saveNotification(record.value());
    }

    private void saveNotification(EventKafkaEvent event) {
        Map<String, Object> payloadsMap = new HashMap<>();
        payloadsMap.put("changes", event.changes());
        NotificationPayloadsEntity payload = new NotificationPayloadsEntity(
                event.messageId(),
                event.eventType().name(),
                event.eventId(),
                event.occurredAt(),
                event.changedById(),
                event.ownerId(),
                payloadsMap
        );

        NotificationPayloadsEntity savedPayload = notificationPayloadsRepository.save(payload);

        for (int i = 0; i < event.subscribers().size(); i++) {
            NotificationEntity notification = new NotificationEntity(
                    event.subscribers().get(i),
                    savedPayload.getPayloadId()
            );
            notificationsRepository.save(notification);
        }
    }
}
