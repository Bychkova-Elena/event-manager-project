package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.dto.EventCreateUpdateRequestDto;
import dev.sorokin.eventmanager.dto.EventResponseDto;
import dev.sorokin.eventmanager.dto.SearchRequestDto;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.model.SearchFilters;
import dev.sorokin.eventmanager.redis.RedisLockManager;
import dev.sorokin.eventmanager.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final RedisLockManager lockManager;
    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventController(RedisLockManager lockManager, EventService eventService, EventMapper eventMapper) {
        this.lockManager = lockManager;
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventCreateUpdateRequestDto requestDto) {
        Event event = eventService.createEvent(eventMapper.mapFromCreateUpdateRequestDtoToEventModel(requestDto));

        EventResponseDto response = eventMapper.mapFromEventModelToResponseDto(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEventById(@PathVariable("eventId") Long eventId) {
        eventService.cancelEventById(eventId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long eventId) {
        Event event = eventService.findEventById(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(eventMapper.mapFromEventModelToResponseDto(event));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> updateEventById(
            @PathVariable Long eventId,
            @Valid @RequestBody EventCreateUpdateRequestDto requestDto
    ) {
        String lockKey = "event " + eventId;
        String token = lockManager.tryLock(lockKey, Duration.ofMinutes(5));

        if (token == null) {
            throw new ResponseStatusException(
                    HttpStatus.LOCKED,
                    "Блокировка захвачена для объекта %s. Попробуйте позже".formatted(lockKey)
            );
        }

        try {
            Thread.sleep(5000);
            Event event = eventService.updateEventById(
                    eventId,
                    eventMapper.mapFromCreateUpdateRequestDtoToEventModel(requestDto)
            );
            EventResponseDto response = eventMapper.mapFromEventModelToResponseDto(event);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lockManager.unlockLock(lockKey, token);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventResponseDto>> searchEvents(@RequestBody @Valid SearchRequestDto request) {

        SearchFilters filters = eventMapper.mapSearchRequestDtoToSearchFiltersModel(request);
        List<Event> events = eventService.searchEvents(filters);

        List<EventResponseDto> responseDtoList = eventMapper.mapFromEventModelListToResponseDtoList(events);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventResponseDto>> getMyEvents() {

        List<Event> events = eventService.getMyEvents();
        List<EventResponseDto> responseDtoList = eventMapper.mapFromEventModelListToResponseDtoList(events);

        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }
}
