package com.replan.business.usecases.teamMember;

import com.replan.domain.requests.RemoveTeamMemberRequest;
import com.replan.domain.responses.RemoveTeamMemberResponse;

public interface RemoveTeamMemberUseCase {
    RemoveTeamMemberResponse removeTeamMember(RemoveTeamMemberRequest request);
}
