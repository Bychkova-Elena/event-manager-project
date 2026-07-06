package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.dto.CreateUpdateLocationDto;
import dev.sorokin.eventmanager.dto.LocationResponseDto;
import dev.sorokin.eventmanager.mapper.LocationMapper;
import dev.sorokin.eventmanager.model.Location;
import dev.sorokin.eventmanager.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;
    private final LocationMapper mapper;

    public LocationController(LocationService locationService, LocationMapper mapper) {
        this.locationService = locationService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<LocationResponseDto>> getAllLocations() {
        List<Location> locations = locationService.getAllLocations();

        List<LocationResponseDto> response = mapper.mapLocationListToLocationResponseDtoList(locations);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<LocationResponseDto> saveLocation(@Valid @RequestBody CreateUpdateLocationDto location) {
        Location savedLocation = locationService.saveLocation(
                location.getName(),
                location.getAddress(),
                location.getDescription(),
                location.getCapacity()
        );

        LocationResponseDto savedLocationDto = mapper.mapLocationToLocationDto(savedLocation);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedLocationDto);
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<LocationResponseDto> getLocationById(@PathVariable Long locationId) {
        Location location = locationService.findLocationById(locationId);
        LocationResponseDto locationDto = mapper.mapLocationToLocationDto(location);

        return ResponseEntity.status(HttpStatus.OK).body(locationDto);
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteLocationById(@PathVariable Long locationId) {
        locationService.deleteLocationById(locationId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<LocationResponseDto> updateLocationById(
            @PathVariable Long locationId,
            @Valid @RequestBody CreateUpdateLocationDto location
    ) {
        Location updatedLocation = locationService.updateLocationById(
                locationId,
                location.getName(),
                location.getAddress(),
                location.getCapacity(),
                location.getDescription()
        );
        LocationResponseDto locationDto = mapper.mapLocationToLocationDto(updatedLocation);

        return ResponseEntity.status(HttpStatus.OK).body(locationDto);
    }

}
