package com.replan.business.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.replan.business.usecases.team.GetTeamsByOwnerUseCase;
import com.replan.domain.objects.Team;
import com.replan.domain.responses.TeamResponse;
import com.replan.persistance.TeamRepository;
import org.springframework.stereotype.Service;

@Service
public class GetTeamsByOwnerImpl implements GetTeamsByOwnerUseCase {

    private final TeamRepository teamRepository;

    public GetTeamsByOwnerImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public List<TeamResponse> getTeamsByOwner(String ownerId) {
        List <Team> teams = teamRepository.findByOwnerId(ownerId);
        return teams.stream()
                .map(team -> new TeamResponse(
                        team.getId(),
                        team.getTeamName(),
                        team.getGameName(),
                        team.getOwnerId()
                )).collect(Collectors.toList());
    }
}
