package dev.sorokin.eventnotificator.kafka.event;

import dev.sorokin.eventcommon.kafka.EventKafkaEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(EventKafkaListener.class);

    @KafkaListener(topics = "event-topic", containerFactory = "containerFactory")
    public void listenEvents(
            ConsumerRecord<UUID, EventKafkaEvent> record
    ) {
        log.info("get event: event={}", record.value());
        // записать данные в бд
    }
}
