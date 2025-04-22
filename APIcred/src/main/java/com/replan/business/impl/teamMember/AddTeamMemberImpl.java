package com.replan.business.impl.teamMember;

import com.replan.business.mapper.TeamMemberMapper;
import com.replan.business.usecases.teamMember.AddTeamMemberUseCase;
import com.replan.domain.objects.Team;
import com.replan.domain.objects.TeamMember;
import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.responses.AddTeamMemberResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.entity.TeamEntity;
import com.replan.persistance.entity.TeamMemberEntity;
import org.springframework.stereotype.Service;

@Service
public class AddTeamMemberImpl implements AddTeamMemberUseCase {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public AddTeamMemberImpl(TeamRepository teamRepository,
                                   TeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    public AddTeamMemberResponse addTeamMember(AddTeamMemberRequest request) {
        // Check if the team actually exists
        TeamEntity team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        
        TeamMemberEntity teamMember = TeamMemberMapper.toEntity(request);

        TeamMemberEntity saved = teamMemberRepository.save(teamMember);

        return TeamMemberMapper.toResponse(saved);
    }

}
