// src/main/java/com/wydeline/repository/PaiementLogRepository.java
package com.wydeline.repository;

import com.wydeline.model.PaiementLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaiementLogRepository extends JpaRepository<PaiementLog, Long> {
    Optional<PaiementLog> findByStripeEventId(String eventId);
}
