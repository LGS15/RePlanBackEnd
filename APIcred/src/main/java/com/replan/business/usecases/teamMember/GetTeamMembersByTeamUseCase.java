package com.replan.business.usecases.teamMember;

import com.replan.domain.requests.GetTeamMembersByTeamRequest;
import com.replan.domain.responses.GetTeamMembersByTeamResponse;

public interface GetTeamMembersByTeamUseCase {
    GetTeamMembersByTeamResponse getTeamMembers(GetTeamMembersByTeamRequest request);
}
