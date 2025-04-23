package com.replan;

import com.replan.business.impl.team.CreateTeamImpl;
import com.replan.business.impl.team.GetTeamsByOwnerImpl;
import com.replan.business.impl.teamMember.AddTeamMemberImpl;
import com.replan.business.impl.teamMember.GetTeamMembersByTeamImpl;
import com.replan.business.usecases.team.CreateTeamUseCase;
import com.replan.business.usecases.team.GetTeamsByOwnerUseCase;
import com.replan.business.usecases.teamMember.AddTeamMemberUseCase;
import com.replan.business.usecases.teamMember.GetTeamMembersByTeamUseCase;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TeamTestConfig {

    @Bean @Primary
    public TeamRepository teamRepository() {
        return Mockito.mock(TeamRepository.class);
    }

    @Bean @Primary
    public TeamMemberRepository teamMemberRepository() {
        return Mockito.mock(TeamMemberRepository.class);
    }

    @Bean
    public CreateTeamUseCase createTeamUseCase(TeamRepository teamRepository) {
        return new CreateTeamImpl(teamRepository);
    }

    @Bean
    public AddTeamMemberUseCase addTeamMemberUseCase(
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository
    ) {
        return new AddTeamMemberImpl(teamRepository, teamMemberRepository);
    }

    @Bean
    public GetTeamsByOwnerUseCase getTeamsByOwnerUseCase(TeamRepository teamRepository) {
        return new GetTeamsByOwnerImpl(teamRepository);
    }

    @Bean
    public GetTeamMembersByTeamUseCase getTeamMembersByTeamUseCase(
            TeamMemberRepository teamMemberRepository
    ) {
        return new GetTeamMembersByTeamImpl(teamMemberRepository);
    }
}