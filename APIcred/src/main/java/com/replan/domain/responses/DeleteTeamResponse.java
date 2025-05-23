package com.replan.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteTeamResponse {
    private String teamId;
    private String teamName;
    private boolean deleted;
    private String message;
}
