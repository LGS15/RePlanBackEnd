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

    // FYI: only used for one native query with partial data,
    // so I left it inside the repo — hope that makes sense.

    interface FocusCombinationStats {
        String getFocusOne();
        String getFocusTwo();
        String getFocusThree();
        Long getCnt();
    }

    @Query(value = "SELECT focus_priority_1 as focusOne, focus_priority_2 as focusTwo, " +
            "focus_priority_3 as focusThree, COUNT(*) as cnt FROM PracticePlanRequest " +
            "WHERE practice_type = :type GROUP BY focus_priority_1, focus_priority_2, focus_priority_3 " +
            "ORDER BY cnt DESC LIMIT 1", nativeQuery = true)
    FocusCombinationStats findMostPopularCombination(@Param("type") String type);
}
