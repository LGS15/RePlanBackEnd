package com.replan.persistance;

import com.replan.persistance.entity.ReviewSessionParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewSessionParticipantRepository extends JpaRepository<ReviewSessionParticipantEntity, UUID> {

    List<ReviewSessionParticipantEntity> findBySessionIdAndIsActive(UUID sessionId, Boolean isActive);

    Optional<ReviewSessionParticipantEntity> findBySessionIdAndUserIdAndIsActive(UUID sessionId, UUID userId, Boolean isActive);

    @Modifying
    @Query("UPDATE ReviewSessionParticipantEntity p SET p.isActive = false, p.leftAt = :leftAt WHERE p.sessionId = :sessionId AND p.userId = :userId AND p.isActive = true")
    void markParticipantAsLeft(@Param("sessionId") UUID sessionId, @Param("userId") UUID userId, @Param("leftAt") LocalDateTime leftAt);

    @Query("SELECT COUNT(p) FROM ReviewSessionParticipantEntity p WHERE p.sessionId = :sessionId AND p.isActive = true")
    Long countActiveParticipants(@Param("sessionId") UUID sessionId);

    @Modifying
    @Query("DELETE FROM ReviewSessionParticipantEntity p WHERE p.sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") UUID sessionId);
}

