package dev.sorokin.eventmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateUpdateLocationDto {
    @NotNull
    private String name;
    @NotNull
    private String address;
    private String description;
    @NotNull
    @Min(5)
    private int capacity;

    public CreateUpdateLocationDto(String name, String address, String description, int capacity) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
