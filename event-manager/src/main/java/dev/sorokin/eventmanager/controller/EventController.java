package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.dto.EventCreateRequestDto;
import dev.sorokin.eventmanager.dto.EventCreateResponseDto;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @PostMapping
    public ResponseEntity<EventCreateResponseDto> createEvent(@Valid @RequestBody EventCreateRequestDto requestDto) {
        Event event = eventService.createEvent(eventMapper.mapFromCreateRequestDtoToEventModel(requestDto));

        EventCreateResponseDto response = eventMapper.mapFromEventModelToCreateResponseDto(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEventById(@PathVariable("eventId") Long eventId) {
        eventService.cancelEventById(eventId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
