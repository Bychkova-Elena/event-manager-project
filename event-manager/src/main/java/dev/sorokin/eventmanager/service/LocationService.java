package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import dev.sorokin.eventmanager.model.Location;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.LocationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final EventRepository eventRepository;

    public LocationService(
            LocationRepository locationRepository,
            LocationMapper locationMapper,
            EventRepository eventRepository
    ) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.eventRepository = eventRepository;
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
        LocationEntity locationEntity = locationRepository.findById(locationId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Локация %s не найдена", locationId)));

        return locationMapper.mapLocationEntityToLocation(locationEntity);
    }

    @Transactional
    public void deleteLocationById(Long locationId) {
        LocationEntity locationEntity = locationRepository.findById(locationId).orElseThrow();

        if (eventRepository.existsByLocation_Id(locationId)) {
            throw new IllegalArgumentException("Невозможно удалить локацию. На локации есть мероприятие");
        }

        locationRepository.delete(locationEntity);
    }

    @Transactional
    public Location updateLocationById(
            Long locationId,
            String name,
            String address,
            int capacity,
            String description
    ) {
        LocationEntity location = locationRepository.findById(locationId).orElseThrow();

        List<EventEntity> events = eventRepository.getAllByLocation_Id(locationId);

        for(EventEntity e : events) {
            if (e.getMaxPlaces() > capacity) {
                throw new IllegalArgumentException(String.format("Невозможно поменять локацию. Мероприятия %s имеет maxPlaces %s > нового капасити", e.getId(), e.getMaxPlaces()));
            }
        }

        location.setName(name);
        location.setAddress(address);
        location.setCapacity(capacity);
        location.setDescription(description);

        LocationEntity updatedLocation = locationRepository.save(location);

        return locationMapper.mapLocationEntityToLocation(updatedLocation);
    }
}
