package dev.sorokin.eventmanager.model;

import dev.sorokin.eventmanager.enums.EventStatus;

public record SearchFilters (
        String name,
        Integer placesMin,
        Integer placesMax,
        String dateStartAfter,
        String dateStartBefore,
        Integer costMin,
        Integer costMax,
        Integer durationMin,
        Integer durationMax,
        Long locationId,
        EventStatus eventStatus
) {
}
