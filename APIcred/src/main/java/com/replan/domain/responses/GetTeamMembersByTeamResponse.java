package com.replan.domain.responses;

import com.replan.persistance.dto.TeamMemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data

public class GetTeamMembersByTeamResponse {
    private List<AddTeamMemberResponse> members;
    private int totalCount;

    public GetTeamMembersByTeamResponse(List<AddTeamMemberResponse> members) {
        this.members = members;
        this.totalCount = members.size();
    }
}
