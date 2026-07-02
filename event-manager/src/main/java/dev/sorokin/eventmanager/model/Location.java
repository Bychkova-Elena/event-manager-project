package dev.sorokin.eventmanager.model;

public record Location(
        Long id,
        String name,
        String address,
        String description,
        int capacity
) {
}
