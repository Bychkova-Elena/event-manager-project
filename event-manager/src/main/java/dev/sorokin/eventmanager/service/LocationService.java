package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import dev.sorokin.eventmanager.model.Location;
import dev.sorokin.eventmanager.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public LocationService(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }

    public List<Location> getAllLocations() {
        List<LocationEntity> entities = locationRepository.findAll();
        return locationMapper.mapLocationEntityListToLocationList(entities);
    }

    public Location saveLocation(String name, String address, String description, int capacity) {
        LocationEntity locationForSave = new LocationEntity(name, address, description, capacity);
        LocationEntity savedLocation = locationRepository.save(locationForSave);

        return locationMapper.mapLocationEntityToLocation(savedLocation);
    }

    public Location findLocationById(Long locationId) {
        LocationEntity locationEntity = locationRepository.findById(locationId).orElseThrow();

        return locationMapper.mapLocationEntityToLocation(locationEntity);
    }

    public void deleteLocationById(Long locationId) {
        LocationEntity locationEntity = locationRepository.findById(locationId).orElseThrow();

        locationRepository.delete(locationEntity);
    }

    public Location updateLocationById(
            Long locationId,
            String name,
            String address,
            int capacity,
            String description
    ) {
        LocationEntity location = locationRepository.findById(locationId).orElseThrow();
        location.setName(name);
        location.setAddress(address);
        location.setCapacity(capacity);
        location.setDescription(description);

        LocationEntity updatedLocation = locationRepository.save(location);

        return locationMapper.mapLocationEntityToLocation(updatedLocation);
    }
}
