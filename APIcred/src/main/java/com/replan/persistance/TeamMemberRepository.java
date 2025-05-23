package com.replan.persistance;


import com.replan.persistance.entity.TeamMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, UUID> {

    List<TeamMemberEntity> findByTeamId(UUID teamId);
    Optional<TeamMemberEntity> findByTeamIdAndUserId(UUID teamId, UUID userId);
    List<TeamMemberEntity> findByUserId(UUID userId);

    @Modifying
    @Query("DELETE FROM TeamMemberEntity tm WHERE tm.teamId = :teamId")
    void deleteByTeamId(@Param("teamId") UUID teamId);
}
