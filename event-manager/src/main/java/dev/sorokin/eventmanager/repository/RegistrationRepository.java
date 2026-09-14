package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.RegistrationEntity;
import dev.sorokin.eventmanager.model.Event;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {
    boolean existsByEvent_IdAndUser_Id(Long id, Long userId);

    RegistrationEntity findByEvent_IdAndUser_Id(Long id, Long userId);

    @Query("""
    SELECT new dev.sorokin.eventmanager.model.Event(
        e.occupiedPlaces, e.date, e.duration, e.cost, e.maxPlaces,
        e.location.id, e.name, e.id, e.owner.id, e.status
    )
    FROM EventEntity e
    JOIN e.registrations r
    WHERE r.user.id = :userId
    """)
    List<Event> findAllEventsByUserId(@Param("userId") Long userId);
}
