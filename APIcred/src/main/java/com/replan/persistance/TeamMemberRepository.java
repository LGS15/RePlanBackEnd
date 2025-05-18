package com.replan.persistance;


import com.replan.persistance.entity.TeamMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, String> {

    List<TeamMemberEntity> findByTeamId(String teamId);
    Optional<TeamMemberEntity> findByTeamIdAndUserId(String teamId, String userId);
    List<TeamMemberEntity> findByUserId(String userId);
}
