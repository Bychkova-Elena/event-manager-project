package dev.sorokin.eventmanager.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventStatusScheduler {

    private final EventService eventService;

    public EventStatusScheduler(EventService eventService) {
        this.eventService = eventService;
    }

    @Scheduled(fixedRate = 60_000)
    public void updateStatuses() {
        eventService.setStartedEventStatus();
        eventService.setFinishedEventStatus();
    }
}
