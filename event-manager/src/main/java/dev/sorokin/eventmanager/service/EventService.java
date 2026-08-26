package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.enums.UserRole;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.model.Location;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
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
        EventEntity eventToSave = eventMapper.mapFromEventModelToEventEntity(event);
        LocationEntity locationEntity = locationMapper.mapLocationModelToLocationEntity(location);
        eventToSave.setLocation(locationEntity);
        eventToSave.setOwner(currentUserEntity);

        EventEntity saved = eventRepository.save(eventToSave);

        logger.info("Successfully saved event: {}", saved);
        return eventMapper.mapFromEventEntityToEventModel(saved);
    }

    @Transactional
    public void cancelEventById(Long eventId) {

        logger.info("Start cancel event: {}", eventId);

        Event event = getEventByIdFromRepository(eventId);

        User currentUser = userService.getCurrentUser();

        validateEventToCancel(event, currentUser);

        LocationEntity location = locationMapper.mapLocationModelToLocationEntity(
                locationService.findLocationById(event.locationId())
        );

        UserEntity owner = userMapper.mapDomainToEntity(
                userService.findUserById(event.ownerId())
        );

        EventEntity entity = eventMapper.mapFromEventModelToEventEntity(event, location, owner);
        entity.setStatus(EventStatus.CANCELLED.name());
        eventRepository.save(entity);

        logger.info("Successfully canceled event: {}", eventId);
    }

    public Event findEventById(Long eventId) {

        logger.info("Start find event with id: {}", eventId);
        Event event = getEventByIdFromRepository(eventId);
        logger.info("Successfully find event: {}", event);

        return event;
    }

    @Transactional
    public Event updateEventById(Long eventId, Event eventToUpdate) {

        logger.info("Start update event with id: {}", eventId);

        Event oldEvent = getEventByIdFromRepository(eventId);
        User currentUser = userService.getCurrentUser();

        validateCurrentUserIsOwnerOrAdmin(oldEvent, currentUser);

        Location newLocation = locationService.findLocationById(eventToUpdate.locationId());

        if (oldEvent.occupiedPlaces() > eventToUpdate.maxPlaces()) {
            logger.error("OccupiedPlaces {} bigger than new event's maxPlaces {}", oldEvent.occupiedPlaces(), eventToUpdate.maxPlaces());
            throw new IllegalArgumentException("Занятых мест - " + oldEvent.occupiedPlaces() + " - больше максимального количества мест на мероприятии - " + eventToUpdate.maxPlaces());
        }

        validateEventToCreateUpdate(eventToUpdate, newLocation);

        UserEntity owner = userMapper.mapDomainToEntity(userService.findUserById(oldEvent.ownerId()));

        EventEntity entity = eventMapper.mapFromEventModelToEventEntity(oldEvent, owner);
        LocationEntity locationEntity = locationMapper.mapLocationModelToLocationEntity(newLocation);

        entity.setName(eventToUpdate.name());
        entity.setMaxPlaces(eventToUpdate.maxPlaces());
        entity.setDate(eventToUpdate.date());
        entity.setCost(eventToUpdate.cost());
        entity.setDuration(eventToUpdate.duration());
        entity.setLocation(locationEntity);

        EventEntity saved = eventRepository.save(entity);
        logger.info("Successfully updated event: {}", saved);

        return eventMapper.mapFromEventEntityToEventModel(saved);
    }

    private Event getEventByIdFromRepository(Long eventId) {
        EventEntity entity = eventRepository
                .findById(eventId)
                .orElseThrow(() -> {
                    logger.error("Not found event with id: {}", eventId);
                    return new NoSuchElementException(String.format("Мероприятие %s не найдено", eventId));
                });

        return eventMapper.mapFromEventEntityToEventModel(entity);
    }

    private void validateCurrentUserIsOwnerOrAdmin(Event event, User currentUser) {
        if (Objects.equals(currentUser.role(), UserRole.USER.name())
                && !Objects.equals(currentUser.id(), event.ownerId())) {

            logger.error("Current user {} not ADMIN and not owner the event {}", currentUser, event);
            throw new AccessDeniedException("Текущий пользователь не является админом или оунером мероприятия");
        }
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

    private void validateEventToCancel(Event event, User currentUser) {
        validateCurrentUserIsOwnerOrAdmin(event, currentUser);

        if (!Objects.equals(event.status(), EventStatus.WAIT_START.name())) {
            logger.error("Event has not status WAIT_START, event's status is {}", event.status());
            throw new IllegalArgumentException("Мероприятие не в статусе WAIT_START");
        }
    }
}
