package com.replan.domain.responses;

import com.replan.persistance.dto.TeamMemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data

public class GetTeamMembersByTeamResponse {
    private List<TeamMemberDto> members;
    private int totalCount;

    public GetTeamMembersByTeamResponse(List<TeamMemberDto> members) {
        this.members = members;
        this.totalCount = members.size();
    }
}
