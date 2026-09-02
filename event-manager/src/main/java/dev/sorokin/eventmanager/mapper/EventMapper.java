package dev.sorokin.eventmanager.mapper;

import dev.sorokin.eventmanager.dto.EventCreateUpdateRequestDto;
import dev.sorokin.eventmanager.dto.EventResponseDto;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.enums.EventStatus;
import dev.sorokin.eventmanager.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event mapFromCreateUpdateRequestDtoToEventModel(EventCreateUpdateRequestDto dto) {
        return new Event(
                0,
                dto.getDate(),
                dto.getDuration(),
                dto.getCost(),
                dto.getMaxPlaces(),
                dto.getLocationId(),
                dto.getName(),
                null,
                null,
                EventStatus.WAIT_START.name()
        );
    }

    public EventResponseDto mapFromEventModelToResponseDto(Event event) {
        return new EventResponseDto(
                event.occupiedPlaces(),
                event.date(),
                event.duration(),
                event.cost(),
                event.maxPlaces(),
                event.locationId(),
                event.name(),
                event.id(),
                event.ownerId(),
                event.status()
        );
    }

    public EventEntity mapFromEventModelToEventEntity(Event event, LocationEntity location, UserEntity owner) {
        return new EventEntity(
                event.id(),
                event.name(),
                event.maxPlaces(),
                event.date(),
                event.cost(),
                event.duration(),
                location,
                owner,
                event.occupiedPlaces(),
                event.status()
        );
    }

    public Event mapFromEventEntityToEventModel(EventEntity entity) {
        return new Event(
                entity.getOccupiedPlaces(),
                entity.getDate(),
                entity.getDuration(),
                entity.getCost(),
                entity.getMaxPlaces(),
                entity.getLocation().getId(),
                entity.getName(),
                entity.getId(),
                entity.getOwner().getId(),
                entity.getStatus()
        );
    }
}
