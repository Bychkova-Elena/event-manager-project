package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.model.Location;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.security.annotation.IsOwnerOrAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final LocationMapper locationMapper;

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    public EventService(
            EventRepository eventRepository,
            LocationService locationService,
            EventMapper eventMapper,
            UserService userService,
            UserMapper userMapper,
            LocationMapper locationMapper
    ) {
        this.eventRepository = eventRepository;
        this.locationService = locationService;
        this.eventMapper = eventMapper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.locationMapper = locationMapper;
    }

    @Transactional
    public Event createEvent(Event event) {

        logger.info("Start create event: {}", event);

        Location location = locationService.findLocationById(event.locationId());

        User currentUser = userService.getCurrentUser();

        validateEventToCreateUpdate(event, location);

        UserEntity currentUserEntity = userMapper.mapDomainToEntity(currentUser);
        LocationEntity locationEntity = locationMapper.mapLocationModelToLocationEntity(location);
        EventEntity eventToSave = eventMapper.mapFromEventModelToEventEntity(event, locationEntity, currentUserEntity);

        EventEntity saved = eventRepository.save(eventToSave);

        logger.info("Successfully saved event: {}", saved);
        return eventMapper.mapFromEventEntityToEventModel(saved);
    }

    @IsOwnerOrAdmin
    @Transactional
    public void cancelEventById(Long eventId) {

        logger.info("Start cancel event: {}", eventId);

        EventEntity event = getEventByIdFromRepository(eventId);

        validateEventToCancel(event);

        event.setStatus(EventStatus.CANCELLED.name());
        eventRepository.save(event);

        logger.info("Successfully canceled event: {}", eventId);
    }

    public Event findEventById(Long eventId) {

        logger.info("Start find event with id: {}", eventId);
        EventEntity event = getEventByIdFromRepository(eventId);
        logger.info("Successfully find event: {}", event);

        return eventMapper.mapFromEventEntityToEventModel(event);
    }

    @IsOwnerOrAdmin
    @Transactional
    public Event updateEventById(Long eventId, Event eventToUpdate) {

        logger.info("Start update event with id: {}", eventId);

        EventEntity oldEvent = getEventByIdFromRepository(eventId);

        Location newLocation = locationService.findLocationById(eventToUpdate.locationId());

        if (oldEvent.getOccupiedPlaces() > eventToUpdate.maxPlaces()) {
            logger.error("OccupiedPlaces {} bigger than new event's maxPlaces {}", oldEvent.getOccupiedPlaces(), eventToUpdate.maxPlaces());
            throw new IllegalArgumentException("Занятых мест - " + oldEvent.getOccupiedPlaces() + " - больше максимального количества мест на мероприятии - " + eventToUpdate.maxPlaces());
        }

        validateEventToCreateUpdate(eventToUpdate, newLocation);

        LocationEntity locationEntity = locationMapper.mapLocationModelToLocationEntity(newLocation);

        oldEvent.setName(eventToUpdate.name());
        oldEvent.setMaxPlaces(eventToUpdate.maxPlaces());
        oldEvent.setDate(eventToUpdate.date());
        oldEvent.setCost(eventToUpdate.cost());
        oldEvent.setDuration(eventToUpdate.duration());
        oldEvent.setLocation(locationEntity);

        EventEntity saved = eventRepository.save(oldEvent);
        logger.info("Successfully updated event: {}", saved);

        return eventMapper.mapFromEventEntityToEventModel(saved);
    }

    private EventEntity getEventByIdFromRepository(Long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> {
                    logger.error("Not found event with id: {}", eventId);
                    return new NoSuchElementException(String.format("Мероприятие %s не найдено", eventId));
                });
    }

    private void validateEventToCreateUpdate(Event newEvent, Location location) {
        if (newEvent.maxPlaces() > location.capacity()) {
            logger.error("MaxPlaces {} bigger than location's capacity {}", newEvent.maxPlaces(), location.capacity());
            throw new IllegalArgumentException("Вместимость локации - " + location.capacity() + " меньше максимального количества мест на мероприятии - " + newEvent.maxPlaces());
        }

        Instant eventInstant = Instant.parse(newEvent.date());

        if (eventInstant.isBefore(Instant.now())) {
            logger.error("Event's date in past: {}", newEvent.date());
            throw new IllegalArgumentException("Дата не должна быть в прошлом. Указана: " + newEvent.date());
        }
    }

    private void validateEventToCancel(EventEntity event) {
        if (!Objects.equals(event.getStatus(), EventStatus.WAIT_START.name())) {
            logger.error("Event has not status WAIT_START, event's status is {}", event.getStatus());
            throw new IllegalArgumentException("Мероприятие не в статусе WAIT_START");
        }
    }
}
