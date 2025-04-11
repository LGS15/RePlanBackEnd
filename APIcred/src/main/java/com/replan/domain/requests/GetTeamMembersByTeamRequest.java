package com.replan.domain.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTeamMembersByTeamRequest {
    private String teamId;
    private String ownerId;
}
