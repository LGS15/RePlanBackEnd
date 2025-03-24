package com.replan.domain.responses;

import com.replan.domain.objects.Role;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AddTeamMemberResponse {
    private String teamMemberId;
    private String teamId;
    private String userId;
    private Role role;
}
