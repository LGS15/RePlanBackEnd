package com.replan.business.impl.teamMember;

import com.replan.business.mapper.TeamMemberMapper;
import com.replan.business.usecases.teamMember.GetTeamMembersByTeamUseCase;
import com.replan.domain.requests.GetTeamMembersByTeamRequest;
import com.replan.domain.responses.AddTeamMemberResponse;
import com.replan.domain.responses.GetTeamMembersByTeamResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.TeamMemberEntity;
import com.replan.persistance.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GetTeamMembersByTeamImpl implements GetTeamMembersByTeamUseCase {

    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    public GetTeamMembersByTeamImpl(TeamMemberRepository teamMemberRepository, UserRepository userRepository) {
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GetTeamMembersByTeamResponse getTeamMembers(GetTeamMembersByTeamRequest request) {
        List<TeamMemberEntity> teamMembers = teamMemberRepository
                .findByTeamId(UUID.fromString(request.getTeamId()));

        List<AddTeamMemberResponse> members = teamMembers.stream()
                .map(teamMember -> {
                    UserEntity user = userRepository.findById(teamMember.getUserId()).orElse(null);
                    return TeamMemberMapper.toResponse(teamMember, user);
                })
                .collect(Collectors.toList());

        return new GetTeamMembersByTeamResponse(members);
    }
}
