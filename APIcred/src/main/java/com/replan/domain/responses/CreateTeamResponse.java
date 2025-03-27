package com.replan.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTeamResponse {
    private String teamId;
    private String teamName;
    private String gameName;
    private String ownerId;
}
