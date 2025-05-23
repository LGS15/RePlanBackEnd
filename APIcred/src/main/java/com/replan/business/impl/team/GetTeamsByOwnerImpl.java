package com.replan.business.impl.team;

import java.util.List;
import java.util.UUID;


import com.replan.business.usecases.team.GetTeamsByOwnerUseCase;

import com.replan.domain.responses.TeamResponse;
import com.replan.persistance.TeamRepository;
import org.springframework.stereotype.Service;
import com.replan.business.mapper.TeamMapper;


@Service
public class GetTeamsByOwnerImpl implements GetTeamsByOwnerUseCase {

    private final TeamRepository teamRepository;

    public GetTeamsByOwnerImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public List<TeamResponse> getTeamsByOwner(String ownerId) {
        return teamRepository.findByOwnerId(UUID.fromString(ownerId)).stream()
                .map(TeamMapper::toTeamResponse)
                .toList();
    }
}
