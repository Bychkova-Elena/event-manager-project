package dev.sorokin.eventmanager.mapper;

import dev.sorokin.eventmanager.dto.LocationResponseDto;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.model.Location;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LocationMapper {

    public List<Location> mapLocationEntityListToLocationList(List<LocationEntity> entities) {
        List<Location> locations = new ArrayList<>();

        for (LocationEntity entity : entities) {
            locations.add(mapLocationEntityToLocation(entity));
        }

        return locations;
    }

    public Location mapLocationEntityToLocation(LocationEntity entity) {
        return new Location(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getDescription(),
                entity.getCapacity()
        );
    }

    public List<LocationResponseDto> mapLocationListToLocationResponseDtoList(List<Location> locationList) {
        List<LocationResponseDto> locationsDto = new ArrayList<>();

        for (Location location : locationList) {
            locationsDto.add(mapLocationToLocationDto(location));
        }

        return locationsDto;
    }

    public LocationResponseDto mapLocationToLocationDto(Location location) {
        return new LocationResponseDto(
                location.id(),
                location.name(),
                location.address(),
                location.description(),
                location.capacity()
        );
    }
}
