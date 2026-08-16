package dev.sorokin.eventmanager.dto;

public record EventCreateResponseDto (
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
) {}