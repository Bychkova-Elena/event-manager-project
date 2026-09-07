package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.service.EventRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events/registrations")
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;

    public EventRegistrationController(EventRegistrationService eventRegistrationService) {
        this.eventRegistrationService = eventRegistrationService;
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<Void> registerUserOnEvent (@PathVariable("eventId") Long eventId) {

        eventRegistrationService.registerUserOnEventByEventId(eventId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
