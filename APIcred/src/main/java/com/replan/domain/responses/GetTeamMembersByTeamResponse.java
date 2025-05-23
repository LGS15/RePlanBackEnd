package com.replan.domain.responses;


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
