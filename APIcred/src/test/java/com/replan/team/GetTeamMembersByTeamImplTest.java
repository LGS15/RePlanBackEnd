package com.replan.team;

import com.replan.business.impl.teamMember.GetTeamMembersByTeamImpl;
import com.replan.domain.objects.Role;
import com.replan.domain.requests.GetTeamMembersByTeamRequest;
import com.replan.domain.responses.GetTeamMembersByTeamResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.entity.TeamMemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GetTeamMembersByTeamImplTest {

    @Mock
    private TeamMemberRepository tmRepo;
    @InjectMocks
    private GetTeamMembersByTeamImpl subject;

    private static final UUID TEAM_ID = UUID.randomUUID();
    private static final UUID MEMBER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID NONEXISTENT_TEAM_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void whenFound_shouldMapAndCount() {
        var e = new TeamMemberEntity();
        e.setId(MEMBER_ID);
        e.setTeamId(TEAM_ID);
        e.setUserId(USER_ID);
        e.setRole(Role.COACH);

        when(tmRepo.findByTeamId(TEAM_ID)).thenReturn(List.of(e));

        var req = new GetTeamMembersByTeamRequest(TEAM_ID.toString());
        GetTeamMembersByTeamResponse resp = subject.getTeamMembers(req);

        assertThat(resp.getMembers()).hasSize(1);
        var out = resp.getMembers().get(0);
        assertThat(out.getTeamMemberId()).isEqualTo(MEMBER_ID.toString());
        assertThat(out.getRole()).isEqualTo(Role.COACH);
        assertThat(resp.getTotalCount()).isEqualTo(1);
    }

    @Test
    void whenNoneFound_shouldBeEmpty() {
        when(tmRepo.findByTeamId(NONEXISTENT_TEAM_ID)).thenReturn(List.of());
        var resp = subject.getTeamMembers(new GetTeamMembersByTeamRequest(NONEXISTENT_TEAM_ID.toString()));
        assertThat(resp.getMembers()).isEmpty();
        assertThat(resp.getTotalCount()).isZero();
    }
}
