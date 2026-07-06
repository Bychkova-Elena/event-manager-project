package dev.sorokin.eventmanager.dto;

public class LocationResponseDto {
    private Long id;
    private String name;
    private String address;
    private String description;
    private int capacity;

    public LocationResponseDto(Long id, String name, String address, String description, int capacity) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
