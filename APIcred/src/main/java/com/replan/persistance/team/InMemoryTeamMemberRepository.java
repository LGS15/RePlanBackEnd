package com.replan.persistance.team;

import com.replan.domain.objects.TeamMember;
import com.replan.persistance.TeamMemberRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryTeamMemberRepository implements TeamMemberRepository {
    private final Map<String, TeamMember> storage = new HashMap<>();

    @Override
    public TeamMember save(TeamMember teamMember){
        if (teamMember.getId() == null || teamMember.getId().isEmpty()) {
            teamMember.setId(UUID.randomUUID().toString());
        }
        storage.put(teamMember.getId(), teamMember);
        return teamMember;
    }

    @Override
    public Optional<TeamMember> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<TeamMember> findByTeamId(String teamId) {
        return storage.values().stream()
                .filter(member -> member.getTeamId().equals(teamId))
                .collect(Collectors.toList());
    }

}
