package com.replan.business.usecases.teamMember;

import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.responses.AddTeamMemberResponse;

public interface AddTeamMemberUseCase {
    AddTeamMemberResponse addTeamMember(AddTeamMemberRequest request);
}
