package com.replan.business.impl.teamMember;

import com.replan.business.mapper.TeamMemberMapper;
import com.replan.business.usecases.teamMember.GetTeamMembersByTeamUseCase;
import com.replan.domain.requests.GetTeamMembersByTeamRequest;
import com.replan.domain.responses.AddTeamMemberResponse;
import com.replan.domain.responses.GetTeamMembersByTeamResponse;
import com.replan.persistance.TeamMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetTeamMembersByTeamImpl implements GetTeamMembersByTeamUseCase {

    private final TeamMemberRepository teamMemberRepository;

    public GetTeamMembersByTeamImpl(TeamMemberRepository teamMemberRepository) {
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    public GetTeamMembersByTeamResponse getTeamMembers(GetTeamMembersByTeamRequest request) {
       List<AddTeamMemberResponse> members = teamMemberRepository
               .findByTeamId(request.getTeamId())
               .stream()
               .map(TeamMemberMapper::toResponse)
               .collect(Collectors.toList());

       return new GetTeamMembersByTeamResponse(members);
    }


}
