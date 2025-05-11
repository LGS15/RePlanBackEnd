package com.replan.domain.responses;

import com.replan.domain.objects.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class RemoveTeamMemberResponse {
    private String teamMemberId;
    private String teamId;
    private String userId;
    private Role role;
    private boolean removed;
}
