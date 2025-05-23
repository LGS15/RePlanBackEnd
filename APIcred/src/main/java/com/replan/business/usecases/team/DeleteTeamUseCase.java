package com.replan.business.usecases.team;

import com.replan.domain.requests.DeleteTeamRequest;
import com.replan.domain.responses.DeleteTeamResponse;

public interface DeleteTeamUseCase {
    DeleteTeamResponse deleteTeam(DeleteTeamRequest request);
}
