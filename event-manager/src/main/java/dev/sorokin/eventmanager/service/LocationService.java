package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import dev.sorokin.eventmanager.model.Location;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.LocationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final EventRepository eventRepository;
    private final RedisTemplate<String, Location> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "location:";
    private static final long CACHE_TTL = 1;

    public LocationService(
            LocationRepository locationRepository,
            LocationMapper locationMapper,
            EventRepository eventRepository,
            RedisTemplate<String, Location> redisTemplate
    ) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.eventRepository = eventRepository;
        this.redisTemplate = redisTemplate;
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
        String cacheKey = CACHE_KEY_PREFIX + locationId;
        Location locationFromCache = redisTemplate.opsForValue().get(cacheKey);
        if (locationFromCache != null) {
            log.info("Локация из кеша: {}", locationFromCache);
            return  locationFromCache;
        }

        LocationEntity locationEntity = locationRepository.findById(locationId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Локация %s не найдена", locationId)));

        Location locationFromDb = locationMapper.mapLocationEntityToLocation(locationEntity);

        redisTemplate.opsForValue().set(cacheKey, locationFromDb, CACHE_TTL, TimeUnit.MINUTES);

        return locationFromDb;
    }

    @Transactional
    public void deleteLocationById(Long locationId) {
        LocationEntity locationEntity = locationRepository.findById(locationId).orElseThrow();

        if (eventRepository.existsByLocation_Id(locationId)) {
            throw new IllegalArgumentException("Невозможно удалить локацию. На локации есть мероприятие");
        }

        locationRepository.delete(locationEntity);
        String cacheKey = CACHE_KEY_PREFIX + locationId;
        redisTemplate.delete(cacheKey);
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
        String cacheKey = CACHE_KEY_PREFIX + locationId;
        redisTemplate.delete(cacheKey);

        return locationMapper.mapLocationEntityToLocation(updatedLocation);
    }
}
