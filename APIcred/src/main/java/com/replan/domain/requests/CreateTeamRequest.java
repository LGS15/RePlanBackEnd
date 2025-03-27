package com.replan.domain.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTeamRequest {
    private String teamName;
    private String gameName;
    private String ownerId;
}
