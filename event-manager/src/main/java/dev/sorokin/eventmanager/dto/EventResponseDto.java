package dev.sorokin.eventmanager.dto;

public record EventResponseDto(
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