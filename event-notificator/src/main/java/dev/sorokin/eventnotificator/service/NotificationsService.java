package dev.sorokin.eventnotificator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventnotificator.entity.NotificationEntity;
import dev.sorokin.eventnotificator.entity.NotificationPayloadsEntity;
import dev.sorokin.eventnotificator.mapper.NotificationsMapper;
import dev.sorokin.eventnotificator.model.Notification;
import dev.sorokin.eventnotificator.repository.NotificationPayloadsRepository;
import dev.sorokin.eventnotificator.repository.NotificationsRepository;
import dev.sorokin.eventnotificator.security.jwt.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationsService {

    private final NotificationsRepository notificationsRepository;
    private final NotificationPayloadsRepository notificationPayloadsRepository;
    private final NotificationsMapper notificationsMapper;

    private static final Logger log = LoggerFactory.getLogger(NotificationsService.class);

    public NotificationsService(
            NotificationsRepository notificationsRepository,
            NotificationPayloadsRepository notificationPayloadsRepository,
            NotificationsMapper notificationsMapper
    ) {
        this.notificationsRepository = notificationsRepository;
        this.notificationPayloadsRepository = notificationPayloadsRepository;
        this.notificationsMapper = notificationsMapper;
    }

    public List<Notification> getNotifications() throws JsonProcessingException {
        UserPrincipal currentUser = getCurrentUser();
        List<Notification> notifications = new ArrayList<>();

        log.info("Getting notifications started for user: {}", currentUser);

        List<NotificationEntity> entities = notificationsRepository.findAllByUserId(currentUser.userId());

        for (NotificationEntity entity : entities) {
            NotificationPayloadsEntity payloads = notificationPayloadsRepository
                    .findById(entity.getPayloadId())
                    .orElseThrow();

            Notification notification = notificationsMapper.mapToNotificationModel(entity, payloads);

            notifications.add(notification);
        }

        log.info("Get notifications: {} for user: {}", notifications, currentUser.login());

        return notifications;
    }

    private UserPrincipal getCurrentUser() {
        UserPrincipal currentUser;
        try {
            currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return currentUser;
    }
}
