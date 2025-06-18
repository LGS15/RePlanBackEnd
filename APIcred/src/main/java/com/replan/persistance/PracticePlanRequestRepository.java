package com.replan.persistance;

import com.replan.persistance.entity.PracticePlanRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PracticePlanRequestRepository extends JpaRepository<PracticePlanRequestEntity, UUID> {

    List<PracticePlanRequestEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<PracticePlanRequestEntity> findByTeamIdOrderByCreatedAtDesc(UUID teamId);

    @Query("SELECT pr FROM PracticePlanRequestEntity pr WHERE pr.userId = :userId AND pr.createdAt >= :since ORDER BY pr.createdAt DESC")
    List<PracticePlanRequestEntity> findByUserIdSince(@Param("userId") UUID userId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(pr) FROM PracticePlanRequestEntity pr WHERE pr.userId = :userId AND pr.createdAt >= :since")
    Long countByUserIdSince(@Param("userId") UUID userId, @Param("since") LocalDateTime since);
}
