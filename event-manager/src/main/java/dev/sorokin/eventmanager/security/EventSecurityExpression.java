package dev.sorokin.eventmanager.security;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.enums.UserRole;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.service.UserService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component("eventSecurity")
public class EventSecurityExpression {

    private final EventRepository eventRepository;
    private final UserService userService;

    public EventSecurityExpression(EventRepository eventRepository, UserService userService) {
        this.eventRepository = eventRepository;
        this.userService = userService;
    }

    public boolean isOwnerOrAdmin(Long eventId) {
        User currentUser = userService.getCurrentUser();

        if (UserRole.ADMIN.name().equals(currentUser.role())) {
            return true;
        }

        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Мероприятие не найдено"));

        return currentUser.id().equals(event.getOwner().getId());
    }
}
