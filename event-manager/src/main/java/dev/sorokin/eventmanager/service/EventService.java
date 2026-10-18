package dev.sorokin.eventmanager.service;

import dev.sorokin.eventcommon.kafka.EventKafkaEvent;
import dev.sorokin.eventcommon.kafka.EventType;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.kafka.event.EventKafkaEventSender;
import dev.sorokin.eventmanager.mapper.EventMapper;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.model.Location;
import dev.sorokin.eventmanager.model.SearchFilters;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.security.annotation.IsOwnerOrAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final LocationMapper locationMapper;
    private final EventKafkaEventSender eventKafkaEventSender;
    private final EventRegistrationService eventRegistrationService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            .withZone(ZoneOffset.UTC);

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    public EventService(
            EventRepository eventRepository,
            LocationService locationService,
            EventMapper eventMapper,
            UserService userService,
            UserMapper userMapper,
            LocationMapper locationMapper,
            EventKafkaEventSender eventKafkaEventSender,
            EventRegistrationService eventRegistrationService
    ) {
        this.eventRepository = eventRepository;
        this.locationService = locationService;
        this.eventMapper = eventMapper;
        this.userService = userService;
        this.userMapper = userMapper;
        this.locationMapper = locationMapper;
        this.eventKafkaEventSender = eventKafkaEventSender;
        this.eventRegistrationService = eventRegistrationService;
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

        User currentUser = userService.getCurrentUser();

        EventEntity event = getEventByIdFromRepository(eventId);

        validateEventToCancel(event);

        event.setStatus(EventStatus.CANCELLED.name());
        eventRepository.save(event);

        List<Long> subscribers = eventRegistrationService.getUserIdsByEventId(eventId);

        List<EventKafkaEvent.FieldChange> changes = List.of(
                new EventKafkaEvent.FieldChange(
                        "status",
                        EventStatus.WAIT_START.name(),
                        EventStatus.CANCELLED.name()
                )
        );

        eventKafkaEventSender.sendEvent(new EventKafkaEvent(
                UUID.randomUUID(),
                EventType.EVENT_UPDATED,
                eventId,
                Instant.now(),
                event.getOwner().getId(),
                currentUser.id(),
                subscribers,
                changes
        ));

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

        User currentUser = userService.getCurrentUser();

        EventEntity oldEvent = getEventByIdFromRepository(eventId);

        Location newLocation = locationService.findLocationById(eventToUpdate.locationId());

        if (oldEvent.getOccupiedPlaces() > eventToUpdate.maxPlaces()) {
            logger.error("OccupiedPlaces {} bigger than new event's maxPlaces {}", oldEvent.getOccupiedPlaces(), eventToUpdate.maxPlaces());
            throw new IllegalArgumentException("Занятых мест - " + oldEvent.getOccupiedPlaces() + " - больше максимального количества мест на мероприятии - " + eventToUpdate.maxPlaces());
        }

        validateEventToCreateUpdate(eventToUpdate, newLocation);

        LocationEntity locationEntity = locationMapper.mapLocationModelToLocationEntity(newLocation);

        List<EventKafkaEvent.FieldChange> changes = getChanges(oldEvent, eventToUpdate);

        oldEvent.setName(eventToUpdate.name());
        oldEvent.setMaxPlaces(eventToUpdate.maxPlaces());
        oldEvent.setDate(eventToUpdate.date());
        oldEvent.setCost(eventToUpdate.cost());
        oldEvent.setDuration(eventToUpdate.duration());
        oldEvent.setLocation(locationEntity);

        EventEntity saved = eventRepository.save(oldEvent);
        logger.info("Successfully updated event: {}", saved);

        List<Long> subscribers = eventRegistrationService.getUserIdsByEventId(eventId);

        eventKafkaEventSender.sendEvent(new EventKafkaEvent(
                UUID.randomUUID(),
                EventType.EVENT_UPDATED,
                eventId,
                Instant.now(),
                saved.getOwner().getId(),
                currentUser.id(),
                subscribers,
                changes
        ));

        return eventMapper.mapFromEventEntityToEventModel(saved);
    }

    public List<Event> searchEvents(SearchFilters filters) {
        logger.info("Start search events by filters: {}", filters);

        String status = filters.eventStatus() != null ? filters.eventStatus().name() : null;

        List<EventEntity> entities = eventRepository.searchEventEntitiesByFilters(
                filters.name(),
                filters.placesMin(),
                filters.placesMax(),
                filters.dateStartAfter(),
                filters.dateStartBefore(),
                filters.costMin(),
                filters.costMax(),
                filters.durationMin(),
                filters.durationMax(),
                filters.locationId(),
                status
        );

        List<Event> events = eventMapper.mapFromEventEntityListToEventList(entities);

        logger.info("Successfully searched events: {}", events);

        return events;
    }

    public List<Event> getMyEvents() {

        logger.info("Start get my events");

        User owner = userService.getCurrentUser();
        List<EventEntity> entities = eventRepository.getAllByOwner_Id(owner.id());
        List<Event> events = eventMapper.mapFromEventEntityListToEventList(entities);

        logger.info("Successfully get events: {}", events);

        return events;
    }

    @Transactional
    public void setStartedEventStatus() {
        logger.info("Start setStartedEventStatus");

        String now = OffsetDateTime.now(ZoneOffset.UTC).format(FORMATTER);

        List<EventEntity> events = eventRepository
                .getAllByStatusAndDateBefore(EventStatus.WAIT_START.name(), now);

        if (events.isEmpty()) {
            logger.info("No events to move to STARTED");
            return;
        }

        logger.info("Found {} events to move from WAIT_START to STARTED", events.size());

        events.forEach(e -> {
            e.setStatus(EventStatus.STARTED.name());
            logger.info("Event {} moved to STARTED", e.getId());

            List<Long> subscribers = eventRegistrationService.getUserIdsByEventId(e.getId());

            List<EventKafkaEvent.FieldChange> changes = List.of(
                    new EventKafkaEvent.FieldChange(
                            "status",
                            EventStatus.WAIT_START.name(),
                            EventStatus.STARTED.name()
                    )
            );

            eventKafkaEventSender.sendEvent(new EventKafkaEvent(
                UUID.randomUUID(),
                EventType.EVENT_UPDATED,
                e.getId(),
                Instant.now(),
                e.getOwner().getId(),
                null,
                subscribers,
                changes
            ));
        });

        logger.info("Successfully finished setStartedEventStatus, updated {} events", events.size());
    }


    @Transactional
    public void setFinishedEventStatus() {
        logger.info("Start setFinishedEventStatus");

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        List<EventEntity> events = eventRepository
                .getAllByStatus(EventStatus.STARTED.name());

        if (events.isEmpty()) {
            logger.info("No STARTED events found");
            return;
        }

        List<EventEntity> eventsToFinish = events.stream()
                .filter(e -> OffsetDateTime.parse(e.getDate(), FORMATTER)
                        .plusMinutes(e.getDuration())
                        .isBefore(now))
                .toList();

        if (eventsToFinish.isEmpty()) {
            logger.info("No events to move to FINISHED");
            return;
        }

        logger.info("Found {} events to move from STARTED to FINISHED", eventsToFinish.size());

        eventsToFinish.forEach(e -> {
            e.setStatus(EventStatus.FINISHED.name());
            logger.info("Event {} moved to FINISHED", e.getId());

            List<Long> subscribers = eventRegistrationService.getUserIdsByEventId(e.getId());

            List<EventKafkaEvent.FieldChange> changes = List.of(
                    new EventKafkaEvent.FieldChange(
                            "status",
                            EventStatus.STARTED.name(),
                            EventStatus.FINISHED.name()
                    )
            );

            eventKafkaEventSender.sendEvent(new EventKafkaEvent(
                    UUID.randomUUID(),
                    EventType.EVENT_UPDATED,
                    e.getId(),
                    Instant.now(),
                    e.getOwner().getId(),
                    null,
                    subscribers,
                    changes
            ));
        });

        logger.info("Successfully finished setFinishedEventStatus, updated {} events", eventsToFinish.size());
    }

    private List<EventKafkaEvent.FieldChange> getChanges(EventEntity oldEvent, Event eventToUpdate) {
        List<EventKafkaEvent.FieldChange> changes = new ArrayList<>();

        if (!Objects.equals(oldEvent.getName(), eventToUpdate.name())) {
            changes.add(new EventKafkaEvent.FieldChange(
                    "name",
                    oldEvent.getName(),
                    eventToUpdate.name()
            ));
        }

        if (!Objects.equals(oldEvent.getMaxPlaces(), eventToUpdate.maxPlaces())) {
            changes.add(new EventKafkaEvent.FieldChange(
                    "maxPlaces",
                    String.valueOf(oldEvent.getMaxPlaces()),
                    String.valueOf(eventToUpdate.maxPlaces())
            ));
        }

        if (!Objects.equals(oldEvent.getDate(), eventToUpdate.date())) {
            changes.add(new EventKafkaEvent.FieldChange(
                    "date",
                    oldEvent.getDate() != null ? oldEvent.getDate() : null,
                    eventToUpdate.date() != null ? eventToUpdate.date() : null
            ));
        }

        if (!Objects.equals(oldEvent.getCost(), eventToUpdate.cost())) {
            changes.add(new EventKafkaEvent.FieldChange(
                    "cost",
                    String.valueOf(oldEvent.getCost()),
                    String.valueOf(eventToUpdate.cost())
            ));
        }

        if (!Objects.equals(oldEvent.getDuration(), eventToUpdate.duration())) {
            changes.add(new EventKafkaEvent.FieldChange(
                    "duration",
                    String.valueOf(oldEvent.getDuration()),
                    String.valueOf(eventToUpdate.duration())
            ));
        }

        Long oldLocationId = oldEvent.getLocation() != null ? oldEvent.getLocation().getId() : null;
        if (!Objects.equals(oldLocationId, eventToUpdate.locationId())) {
            changes.add(new EventKafkaEvent.FieldChange(
                    "locationId",
                    String.valueOf(oldLocationId),
                    String.valueOf(eventToUpdate.locationId())
            ));
        }

        return changes;
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

        OffsetDateTime eventDateTime = parseAndValidateDate(newEvent.date());

        if (eventDateTime.isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            logger.error("Event's date in past: {}", newEvent.date());
            throw new IllegalArgumentException("Дата не должна быть в прошлом. Указана: " + newEvent.date());
        }
    }

    private OffsetDateTime parseAndValidateDate(String date) {
        try {
            return OffsetDateTime.parse(date, FORMATTER);
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format: {}", date);
            throw new IllegalArgumentException(
                    "Неверный формат даты. Ожидается: yyyy-MM-dd'T'HH:mm:ss.SSSXXX. Получено: " + date
            );
        }
    }

    private void validateEventToCancel(EventEntity event) {
        if (!Objects.equals(event.getStatus(), EventStatus.WAIT_START.name())) {
            logger.error("Event has not status WAIT_START, event's status is {}", event.getStatus());
            throw new IllegalArgumentException("Мероприятие не в статусе WAIT_START");
        }
    }
}
