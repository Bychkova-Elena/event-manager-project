package dev.sorokin.eventnotificator.repository;

import dev.sorokin.eventnotificator.entity.NotificationPayloadsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationPayloadsRepository extends JpaRepository<NotificationPayloadsEntity, Long> {
}
