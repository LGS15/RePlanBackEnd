package com.replan.persistance;

import com.replan.domain.objects.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepository {
    Team save(Team team);
    Optional<Team> findById(String id);
    List<Team> findAll();
    List<Team> findByOwnerId(String ownerId);
}
