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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GetTeamMembersByTeamImplTest {

    @Mock
    private TeamMemberRepository tmRepo;
    @InjectMocks
    private GetTeamMembersByTeamImpl subject;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void whenFound_shouldMapAndCount() {
        var e = new TeamMemberEntity();
        e.setId("m1"); e.setTeamId("t1"); e.setUserId("u1"); e.setRole(Role.COACH);

        when(tmRepo.findByTeamId("t1")).thenReturn(List.of(e));

        var req = new GetTeamMembersByTeamRequest("t1");
        GetTeamMembersByTeamResponse resp = subject.getTeamMembers(req);

        assertThat(resp.getMembers()).hasSize(1);
        var out = resp.getMembers().get(0);
        assertThat(out.getTeamMemberId()).isEqualTo("m1");
        assertThat(out.getRole()).isEqualTo(Role.COACH);
        assertThat(resp.getTotalCount()).isEqualTo(1);
    }

    @Test
    void whenNoneFound_shouldBeEmpty() {
        when(tmRepo.findByTeamId("x")).thenReturn(List.of());
        var resp = subject.getTeamMembers(new GetTeamMembersByTeamRequest("x"));
        assertThat(resp.getMembers()).isEmpty();
        assertThat(resp.getTotalCount()).isZero();
    }
}
