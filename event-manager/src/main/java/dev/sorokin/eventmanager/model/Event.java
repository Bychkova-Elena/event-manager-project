package dev.sorokin.eventmanager.model;

public record Event(
        int occupiedPlaces,
        String date,
        int duration,
        int cost,
        int maxPlaces,
        Long locationId,
        String name,
        Long id,
        Long ownerId,
        String status
) {
}
