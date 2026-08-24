package dev.sorokin.eventmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

public class EventCreateUpdateRequestDto {

    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private int maxPlaces;

    @NotBlank
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String date;

    @NotNull
    @Min(1)
    private int cost;

    @NotNull
    @Min(30)
    private int duration;

    @NotNull
    private Long locationId;

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    @NotNull
    @Min(1)
    public int getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(@NotNull @Min(1) int maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public @NotBlank String getDate() {
        return date;
    }

    public void setDate(@NotBlank String date) {
        this.date = date;
    }

    @NotNull
    @Min(1)
    public int getCost() {
        return cost;
    }

    public void setCost(@NotNull @Min(1) int cost) {
        this.cost = cost;
    }

    @NotNull
    @Min(30)
    public int getDuration() {
        return duration;
    }

    public void setDuration(@NotNull @Min(30) int duration) {
        this.duration = duration;
    }

    public @NotNull Long getLocationId() {
        return locationId;
    }

    public void setLocationId(@NotNull Long locationId) {
        this.locationId = locationId;
    }
}
