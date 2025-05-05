package com.replan.domain.requests;

import com.replan.domain.objects.Role;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTeamMemberRequest {
    private String teamId;
    private String email;
    private Role role;
}
