package com.replan.business.impl.team;

import com.replan.business.mapper.TeamMapper;
import com.replan.business.usecases.team.GetTeamsByUserUseCase;
import com.replan.domain.responses.TeamResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.entity.TeamMemberEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class GetTeamsByUserImpl implements GetTeamsByUserUseCase {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    public GetTeamsByUserImpl(TeamMemberRepository teamMemberRepository, TeamRepository teamRepository) {
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public List<TeamResponse> getTeamsByUser(String userId) {
        List<UUID> teamIds= teamMemberRepository.findByUserId(UUID.fromString(userId)).stream()
                .map(TeamMemberEntity::getTeamId)
                .toList();

        return teamRepository.findAllById(teamIds).stream()
                .map(TeamMapper::toTeamResponse)
                .toList();
    }
}
