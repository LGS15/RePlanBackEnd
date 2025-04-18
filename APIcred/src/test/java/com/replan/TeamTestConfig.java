package com.replan;

import com.replan.business.usecases.team.CreateTeamUseCase;
import com.replan.business.usecases.team.GetTeamsByOwnerUseCase;
import com.replan.business.usecases.teamMember.AddTeamMemberUseCase;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TeamTestConfig {

    @Bean
    public CreateTeamUseCase createTeamUseCase() {
        return Mockito.mock(CreateTeamUseCase.class);
    }

    @Bean
    public AddTeamMemberUseCase addTeamMemberUseCase() {
        return Mockito.mock(AddTeamMemberUseCase.class);
    }

    @Bean
    public GetTeamsByOwnerUseCase getTeamsByOwnerUseCase() {return Mockito.mock(GetTeamsByOwnerUseCase.class);}
}