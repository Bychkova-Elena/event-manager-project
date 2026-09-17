package dev.sorokin.eventmanager.dto;

import dev.sorokin.eventmanager.enums.EventStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;

public class SearchRequestDto {

    @Nullable
    private String name;

    @Nullable
    @Min(1)
    private Integer placesMin;

    @Nullable
    @Min(1)
    private Integer placesMax;

    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String dateStartAfter;

    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String dateStartBefore;

    @Nullable
    @Min(1)
    private Integer costMin;

    @Nullable
    @Min(1)
    private Integer costMax;

    @Nullable
    @Min(30)
    private Integer durationMin;

    @Nullable
    @Min(30)
    private Integer durationMax;

    @Nullable
    private Long locationId;

    @Nullable
    private EventStatus eventStatus;

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public Integer getPlacesMin() {
        return placesMin;
    }

    public void setPlacesMin(@Nullable Integer placesMin) {
        this.placesMin = placesMin;
    }

    @Nullable
    public Integer getPlacesMax() {
        return placesMax;
    }

    public void setPlacesMax(@Nullable Integer placesMax) {
        this.placesMax = placesMax;
    }

    @Nullable
    public String getDateStartAfter() {
        return dateStartAfter;
    }

    public void setDateStartAfter(@Nullable String dateStartAfter) {
        this.dateStartAfter = dateStartAfter;
    }

    @Nullable
    public String getDateStartBefore() {
        return dateStartBefore;
    }

    public void setDateStartBefore(@Nullable String dateStartBefore) {
        this.dateStartBefore = dateStartBefore;
    }

    @Nullable
    public Integer getCostMin() {
        return costMin;
    }

    public void setCostMin(@Nullable Integer costMin) {
        this.costMin = costMin;
    }

    @Nullable
    public Integer getCostMax() {
        return costMax;
    }

    public void setCostMax(@Nullable Integer costMax) {
        this.costMax = costMax;
    }

    @Nullable
    public Integer getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(@Nullable Integer durationMin) {
        this.durationMin = durationMin;
    }

    @Nullable
    public Integer getDurationMax() {
        return durationMax;
    }

    public void setDurationMax(@Nullable Integer durationMax) {
        this.durationMax = durationMax;
    }

    @Nullable
    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(@Nullable Long locationId) {
        this.locationId = locationId;
    }

    @Nullable
    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(@Nullable EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }
}
