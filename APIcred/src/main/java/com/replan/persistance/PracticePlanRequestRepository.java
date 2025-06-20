package com.replan.persistance;

import com.replan.domain.objects.PracticeType;
import com.replan.persistance.entity.PracticePlanRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
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


    // Yes I changed it so it's now JPQL
    @Query("SELECT pr.focusOne as focusOne, pr.focusTwo as focusTwo, pr.focusThree as focusThree, COUNT(pr) as cnt " +
            "FROM PracticePlanRequestEntity pr WHERE pr.practiceType = :type " +
            "GROUP BY pr.focusOne, pr.focusTwo, pr.focusThree ORDER BY COUNT(pr) DESC")
    List<FocusCombinationStats> findMostPopularCombination(@Param("type") PracticeType type, Pageable pageable);
}
