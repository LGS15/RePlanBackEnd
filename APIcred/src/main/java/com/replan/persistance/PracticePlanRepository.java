package com.replan.persistance;

import com.replan.persistance.entity.PracticePlanEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PracticePlanRepository extends JpaRepository<PracticePlanEntity, UUID> {

    Optional<PracticePlanEntity> findByRequestId(UUID requestId);

    @Query("SELECT pp FROM PracticePlanEntity pp " +
            "JOIN PracticePlanRequestEntity pr ON pp.requestId = pr.id " +
            "WHERE pr.userId = :userId ORDER BY pp.generatedAt DESC")
    Page<PracticePlanEntity> findByUserIdOrderByGeneratedAtDesc(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT pp FROM PracticePlanEntity pp " +
            "JOIN PracticePlanRequestEntity pr ON pp.requestId = pr.id " +
            "WHERE pr.teamId = :teamId ORDER BY pp.generatedAt DESC")
    Page<PracticePlanEntity> findByTeamIdOrderByGeneratedAtDesc(@Param("teamId") UUID teamId, Pageable pageable);

    @Query("SELECT pp FROM PracticePlanEntity pp " +
            "JOIN PracticePlanRequestEntity pr ON pp.requestId = pr.id " +
            "WHERE pr.userId = :userId AND pp.generatedAt >= :since ORDER BY pp.generatedAt DESC")
    List<PracticePlanEntity> findByUserIdSince(@Param("userId") UUID userId, @Param("since") LocalDateTime since);
}