package dev.sorokin.eventmanager.entity;

import dev.sorokin.eventmanager.enums.EventStatus;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "event")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "max_places", nullable = false)
    private int maxPlaces;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "cost", nullable = false)
    private int cost;

    @Column(name = "duration", nullable = false)
    private int duration;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(name = "occupied_places", nullable = false)
    private int occupiedPlaces;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "event")
    private List<RegistrationEntity> registrations;

    public EventEntity(
            Long id,
            String name,
            int maxPlaces,
            String date,
            int cost,
            int duration,
            LocationEntity location,
            UserEntity owner,
            int occupiedPlaces,
            String status
    ) {
        this.id = id;
        this.name = name;
        this.maxPlaces = maxPlaces;
        this.date = date;
        this.cost = cost;
        this.duration = duration;
        this.location = location;
        this.owner = owner;
        this.occupiedPlaces = occupiedPlaces;
        this.status = status;
    }

    public EventEntity(
            Long id,
            String name,
            int maxPlaces,
            String date,
            int cost,
            int duration,
            LocationEntity location,
            UserEntity owner
    ) {
        this.id = id;
        this.name = name;
        this.maxPlaces = maxPlaces;
        this.date = date;
        this.cost = cost;
        this.duration = duration;
        this.location = location;
        this.owner = owner;
        this.occupiedPlaces = 0;
        this.status = EventStatus.WAIT_START.name();
    }

    public EventEntity() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(int maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocationEntity getLocation() {
        return location;
    }

    public void setLocation(LocationEntity location) {
        this.location = location;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public int getOccupiedPlaces() {
        return occupiedPlaces;
    }

    public void setOccupiedPlaces(int occupiedPlaces) {
        this.occupiedPlaces = occupiedPlaces;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RegistrationEntity> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<RegistrationEntity> registrations) {
        this.registrations = registrations;
    }
}
