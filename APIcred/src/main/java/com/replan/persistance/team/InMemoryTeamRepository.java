package com.replan.persistance.team;

import com.replan.domain.objects.Team;
import com.replan.persistance.TeamRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryTeamRepository implements TeamRepository {
    private final Map<String, Team> storage= new HashMap<>();

    @Override
    public Team save(Team team) {
        if (team.getId() == null || team.getId().isEmpty())
        {
            team.setId(UUID.randomUUID().toString());
        }
        storage.put(team.getId(), team);
        return team;
    }

    @Override
    public Optional<Team> findById(String id){
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Team> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Team> findByOwnerId(String ownerId) {
        return storage.values().stream()
                .filter(t->t.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }
}
