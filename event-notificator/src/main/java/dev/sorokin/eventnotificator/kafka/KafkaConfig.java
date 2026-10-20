package dev.sorokin.eventnotificator.kafka;

import dev.sorokin.eventcommon.kafka.EventKafkaEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public ConsumerFactory<UUID, EventKafkaEvent> consumerFactory() {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "notificator-group");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);

        var factory = new DefaultKafkaConsumerFactory<UUID, EventKafkaEvent>(configProperties);
        factory.setValueDeserializer(new JsonDeserializer<>(EventKafkaEvent.class, false));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<UUID, EventKafkaEvent> containerFactory(
            ConsumerFactory<UUID, EventKafkaEvent> consumerFactory
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<UUID, EventKafkaEvent>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
