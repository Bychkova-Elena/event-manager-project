package dev.sorokin.eventmanager.kafka.event;

import dev.sorokin.eventcommon.kafka.EventKafkaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventKafkaEventSender {

    private static final Logger logger = LoggerFactory.getLogger(EventKafkaEventSender.class);

    private final KafkaTemplate<UUID, EventKafkaEvent> kafkaTemplate;

    public EventKafkaEventSender(KafkaTemplate<UUID, EventKafkaEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(EventKafkaEvent eventKafkaEvent) {
        logger.info("Sending event: event={}", eventKafkaEvent);

        kafkaTemplate.send(
                "event-topic",
                eventKafkaEvent.messageId(),
                eventKafkaEvent
        );
    }
}
