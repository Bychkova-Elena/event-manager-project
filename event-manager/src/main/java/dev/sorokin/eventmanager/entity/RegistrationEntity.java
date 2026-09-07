package dev.sorokin.eventmanager.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "registration")
public class RegistrationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public RegistrationEntity(Long id, EventEntity event, UserEntity user) {
        this.id = id;
        this.event = event;
        this.user = user;
    }

    public RegistrationEntity() {

    }

    public Long getId() {
        return id;
    }

    public EventEntity getEvent() {
        return event;
    }

    public UserEntity getUser() {
        return user;
    }
}
