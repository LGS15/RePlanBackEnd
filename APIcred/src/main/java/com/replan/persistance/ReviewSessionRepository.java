package com.replan.persistance;

import com.replan.domain.objects.SessionStatus;
import com.replan.persistance.entity.ReviewSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewSessionRepository extends JpaRepository<ReviewSessionEntity, UUID> {

    List<ReviewSessionEntity> findByTeamIdAndStatus(UUID teamId, SessionStatus status);

    List<ReviewSessionEntity> findByTeamId(UUID teamId);

    @Query("SELECT rs FROM ReviewSessionEntity rs WHERE rs.teamId = :teamId AND rs.status = 'ACTIVE'")
    List<ReviewSessionEntity> findActiveSessionsByTeamId(@Param("teamId") UUID teamId);

    Optional<ReviewSessionEntity> findByIdAndTeamId(UUID id, UUID teamId);
}
