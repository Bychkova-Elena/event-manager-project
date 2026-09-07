package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.RegistrationEntity;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.mapper.UserMapper;
import dev.sorokin.eventmanager.model.User;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.RegistrationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class EventRegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    private static final Logger logger = LoggerFactory.getLogger(EventRegistrationService.class);

    public EventRegistrationService(
            RegistrationRepository registrationRepository,
            EventRepository eventRepository,
            UserService userService,
            UserMapper userMapper
    ) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Transactional
    public void registerUserOnEventByEventId(Long eventId) {

        User user = userService.getCurrentUser();
        UserEntity userEntity = userMapper.mapDomainToEntity(user);

        logger.info("Start register user {} on event: {}", userEntity.getId(), eventId);

        EventEntity eventEntity = eventRepository
                .findByIdWithLock(eventId)
                .orElseThrow(() -> {
                    logger.error("Not found event with id: {}", eventId);
                    return new NoSuchElementException(String.format("Мероприятие %s не найдено", eventId));
                });

        validateEventToRegister(eventEntity, userEntity.getId());

        RegistrationEntity newRegistration = new RegistrationEntity(null, eventEntity, userEntity);
        registrationRepository.save(newRegistration);

        eventEntity.setOccupiedPlaces(eventEntity.getOccupiedPlaces()+1);
        eventRepository.save(eventEntity);

        logger.info("Successfully saved registration on event: {} by user: {}", eventId, userEntity.getId());
    }

    private void validateEventToRegister(EventEntity event, Long userId) {
        if (!event.getStatus().equals(EventStatus.WAIT_START.name())) {
            logger.error("Unable to register for the event: {}, the event is not in the WAIT_START status", event.getId());
            throw new IllegalArgumentException(
                    "Невозможно зарегистрироваться на мероприятие: мероприятие не в статусе WAIT_START"
            );
        }

        if (event.getMaxPlaces() <= event.getOccupiedPlaces()) {
            logger.error("Unable to register for the event: {}, fully booked", event.getId());
            throw new IllegalArgumentException("Невозможно зарегистрироваться на мероприятие: нет мест");
        }

        if (registrationRepository.existsByEvent_IdAndUser_Id(event.getId(), userId)) {
            logger.error("You are already registered for event: {}", event.getId());
            throw new IllegalArgumentException("Вы уже зарегистрированы на это мероприятие");
        }
    }
}
