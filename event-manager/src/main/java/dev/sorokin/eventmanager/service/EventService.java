package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.enums.UserRole;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.LocationRepository;
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
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    public EventService(
            EventRepository eventRepository,
            LocationRepository locationRepository,
            EventMapper eventMapper,
            UserService userService,
            UserMapper userMapper
    ) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.eventMapper = eventMapper;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Transactional
    public Event createEvent(Event event) {

        logger.info("Start create event: {}", event);

        LocationEntity location = locationRepository
                .findById(event.locationId())
                .orElseThrow(() -> {
                    logger.error("Not found location with id: {}", event.locationId());
                    return new NoSuchElementException(String.format("Локация %s не найдена", event.locationId()));
                });
        User currentUser = userService.getCurrentUser();

        validateEventToCreate(event, location);

        UserEntity currentUserEntity = userMapper.mapDomainToEntity(currentUser);
        EventEntity eventToSave = eventMapper.mapFromEventModelToEventEntity(event);
        eventToSave.setLocation(location);
        eventToSave.setOwner(currentUserEntity);

        EventEntity saved = eventRepository.save(eventToSave);

        logger.info("Successfully saved event: {}", eventToSave);
        return eventMapper.mapFromEventEntityToEventModel(saved);
    }

    @Transactional
    public void cancelEventById(Long eventId) {

        logger.info("Start cancel event: {}", eventId);

        Event event = getEventByIdFromRepository(eventId);

        User currentUser = userService.getCurrentUser();

        validateEventToCancel(event, currentUser);

        EventEntity entity = eventMapper.mapFromEventModelToEventEntity(event);
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

    private Event getEventByIdFromRepository(Long eventId) {
        EventEntity entity = eventRepository
                .findById(eventId)
                .orElseThrow(() -> {
                    logger.error("Not found event with id: {}", eventId);
                    return new NoSuchElementException(String.format("Мероприятие %s не найдено", eventId));
                });

        return eventMapper.mapFromEventEntityToEventModel(entity);
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

    private void validateEventToCancel(Event event, User currentUser) {
        if (Objects.equals(currentUser.role(), UserRole.USER.name())
                && !Objects.equals(currentUser.id(), event.ownerId())) {

            logger.error("Current user {} not ADMIN and not owner the event {}", currentUser, event);
            throw new AccessDeniedException("Текущий пользователь не является админом или оунером мероприятия");
        }

        if (!Objects.equals(event.status(), EventStatus.WAIT_START.name())) {
            logger.error("Event has not status WAIT_START, event's status is {}", event.status());
            throw new IllegalArgumentException("Мероприятие не в статусе WAIT_START");
        }
    }
}
