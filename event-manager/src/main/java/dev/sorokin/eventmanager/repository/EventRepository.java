package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.EventEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("""
        SELECT e FROM EventEntity e
        WHERE (:name IS NULL OR e.name = :name)
        AND (:placesMin IS NULL OR e.maxPlaces >= :placesMin)
        AND (:placesMax IS NULL OR e.maxPlaces <= :placesMax)
        AND (:dateStartAfter IS NULL OR e.date > :dateStartAfter)
        AND (:dateStartBefore IS NULL OR e.date < :dateStartBefore)
        AND (:costMin IS NULL OR e.cost >= :costMin)
        AND (:costMax IS NULL OR e.cost <= :costMax)
        AND (:durationMin IS NULL OR e.duration >= :durationMin)
        AND (:durationMax IS NULL OR e.duration <= :durationMax)
        AND (:locationId IS NULL OR e.location.id = :locationId)
        AND (:eventStatus IS NULL OR e.status = :eventStatus)
    """)
    List<EventEntity> searchEventEntitiesByFilters(
            @Param("name") String name,
            @Param("placesMin") Integer placesMin,
            @Param("placesMax") Integer placesMax,
            @Param("dateStartAfter") String dateStartAfter,
            @Param("dateStartBefore") String dateStartBefore,
            @Param("costMin") Integer costMin,
            @Param("costMax") Integer costMax,
            @Param("durationMin") Integer durationMin,
            @Param("durationMax") Integer durationMax,
            @Param("locationId") Long locationId,
            @Param("eventStatus") String eventStatus
    );

    List<EventEntity> getAllByOwner_Id(Long ownerId);
}
