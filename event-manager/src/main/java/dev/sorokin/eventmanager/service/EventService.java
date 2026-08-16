package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    public EventService(
            EventRepository eventRepository,
            LocationRepository locationRepository,
            EventMapper eventMapper
    ) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.eventMapper = eventMapper;
    }

    @Transactional
    public Event createEvent(Event event) {

        logger.info("Start create event: {}", event);

        LocationEntity location = locationRepository
                .findById(event.locationId())
                .orElseThrow(() -> new NoSuchElementException("Location id not found " + event.locationId()));
        UserEntity owner = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        validateEventToCreate(event, location);

        EventEntity eventToSave = eventMapper.mapFromEventModelToEventEntity(event);
        eventToSave.setLocation(location);
        eventToSave.setOwner(owner);

        EventEntity saved = eventRepository.save(eventToSave);

        logger.info("Successfully saved event: {}", eventToSave);
        return eventMapper.mapFromEventEntityToEventModel(saved);
    }

    private void validateEventToCreate(Event newEvent, LocationEntity location) {
        if (newEvent.maxPlaces() > location.getCapacity()) {
            logger.error("MaxPlaces {} bigger than location's capacity {}", newEvent.maxPlaces(), location.getCapacity());
            throw new IllegalArgumentException("Вместимость локации - " + location.getCapacity() + " меньше максимального количества мест на мероприятии - " + newEvent.maxPlaces());
        }

        Instant eventInstant = Instant.parse(newEvent.date());

        if (eventInstant.isBefore(Instant.now())) {
            logger.error("Event's date in past: {}", newEvent.date());
            throw new IllegalArgumentException("Дата не должна быть в прошлом. Указана: " + newEvent.date());
        }
    }
}
