package dev.sorokin.eventnotificator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.sorokin.eventnotificator.dto.NotificationResponseDto;
import dev.sorokin.eventnotificator.mapper.NotificationsMapper;
import dev.sorokin.eventnotificator.model.Notification;
import dev.sorokin.eventnotificator.service.NotificationsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    private final NotificationsService notificationsService;
    private final NotificationsMapper notificationsMapper;

    public NotificationsController(NotificationsService notificationsService, NotificationsMapper notificationsMapper) {
        this.notificationsService = notificationsService;
        this.notificationsMapper = notificationsMapper;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getUserNotifications() throws JsonProcessingException {
        List<Notification> notifications = notificationsService.getNotifications();
        return ResponseEntity.status(HttpStatus.OK).body(notificationsMapper.toDtoList(notifications));
    }
}
