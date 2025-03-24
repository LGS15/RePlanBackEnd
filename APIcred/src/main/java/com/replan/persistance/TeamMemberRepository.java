package com.replan.persistance;

import com.replan.domain.objects.TeamMember;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository {
    TeamMember save(TeamMember teamMember);
    Optional<TeamMember> findById(String id);
    List<TeamMember> findByTeamId(String teamId);
}
