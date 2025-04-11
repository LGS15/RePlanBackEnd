package com.replan.business.impl.teamMember;

import com.replan.business.mapper.TeamMemberMapper;
import com.replan.business.usecases.teamMember.GetTeamMembersByTeamUseCase;
import com.replan.domain.objects.TeamMember;
import com.replan.domain.requests.GetTeamMembersByTeamRequest;
import com.replan.domain.responses.GetTeamMembersByTeamResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.dto.TeamMemberDto;

import java.util.List;
import java.util.stream.Collectors;

public class GetTeamMemberByTeamImpl implements GetTeamMembersByTeamUseCase {

    private final TeamMemberRepository teamMemberRepository;

    public GetTeamMemberByTeamImpl(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    public GetTeamMembersByTeamResponse getTeamMembers(GetTeamMembersByTeamRequest request) {
        List<TeamMember> teamMembers = teamMemberRepository.findByTeamId(request.getTeamId());

        List<TeamMemberDto> dtos= teamMembers.stream()
                .map(TeamMemberMapper::toDto)
                .collect(Collectors.toList());

        GetTeamMembersByTeamResponse response = new GetTeamMembersByTeamResponse(dtos);
        return response;
    }
}
