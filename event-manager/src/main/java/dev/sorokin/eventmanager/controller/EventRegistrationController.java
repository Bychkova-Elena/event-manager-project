package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.dto.EventResponseDto;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.service.EventRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;
    private final EventMapper eventMapper;

    public EventRegistrationController(EventRegistrationService eventRegistrationService, EventMapper eventMapper) {
        this.eventRegistrationService = eventRegistrationService;
        this.eventMapper = eventMapper;
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<Void> registerUserOnEvent(@PathVariable("eventId") Long eventId) {

        eventRegistrationService.registerUserOnEventByEventId(eventId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/cancel/{eventId}")
    public ResponseEntity<Void> cancelRegistrationOnEvent(@PathVariable("eventId") Long eventId) {

        eventRegistrationService.deleteRegistrationOnEvent(eventId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventResponseDto>> getMyRegistrations() {

        List<Event> events = eventRegistrationService.getMyRegistrations();
        List <EventResponseDto> dto = eventMapper.mapFromEventModelListToResponseDtoList(events);
        
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
}
