package com.replan.business.impl.teamMember;

import com.replan.business.mapper.TeamMemberMapper;
import com.replan.business.usecases.teamMember.AddTeamMemberUseCase;

import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.responses.AddTeamMemberResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.TeamEntity;
import com.replan.persistance.entity.TeamMemberEntity;
import com.replan.persistance.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class AddTeamMemberImpl implements AddTeamMemberUseCase {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    public AddTeamMemberImpl(TeamRepository teamRepository,
                                   TeamMemberRepository teamMemberRepository,
                             UserRepository userRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    public AddTeamMemberResponse addTeamMember(AddTeamMemberRequest request) {
        // Check if the team actually exists
        TeamEntity team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User with email " + request.getEmail() + " not found"));
        
        TeamMemberEntity teamMember = TeamMemberMapper.toEntity(request,user.getId());

        TeamMemberEntity saved = teamMemberRepository.save(teamMember);

        return TeamMemberMapper.toResponse(saved);
    }

}
