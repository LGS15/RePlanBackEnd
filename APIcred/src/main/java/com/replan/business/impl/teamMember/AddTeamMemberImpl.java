package com.replan.business.impl.teamMember;

import com.replan.business.usecases.teamMember.AddTeamMemberUseCase;
import com.replan.domain.objects.Team;
import com.replan.domain.objects.TeamMember;
import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.responses.AddTeamMemberResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
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
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        
        TeamMember newMember = new TeamMember(request.getTeamId(), request.getUserId(), request.getRole());


        TeamMember savedMember = teamMemberRepository.save(newMember);


        return new AddTeamMemberResponse(
                savedMember.getId(),
                savedMember.getTeamId(),
                savedMember.getUserId(),
                savedMember.getRole()
        );
    }

}
