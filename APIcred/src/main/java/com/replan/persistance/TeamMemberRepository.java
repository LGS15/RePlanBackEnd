package com.replan.persistance;


import com.replan.persistance.entity.TeamMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, String> {

    List<TeamMemberEntity> findByTeamId(String teamId);
    Optional<TeamMemberEntity> findByTeamIdAndUserId(String teamId, String userId);
    List<TeamMemberEntity> findByUserId(String userId);

    @Modifying
    @Query("DELETE FROM TeamMemberEntity tm WHERE tm.teamId = :teamId")
    void deleteByTeamId(@Param("teamId") String teamId);
}
