package dev.sorokin.eventmanager.repository;

import dev.sorokin.eventmanager.entity.RegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {
    boolean existsByEvent_IdAndUser_Id(Long id, Long userId);

    RegistrationEntity findByEvent_IdAndUser_Id(Long id, Long userId);
}
