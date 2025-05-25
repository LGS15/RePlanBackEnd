package com.replan;

import com.replan.business.impl.team.CreateTeamImpl;
import com.replan.business.impl.team.DeleteTeamImpl;
import com.replan.business.impl.team.GetTeamsByOwnerImpl;
import com.replan.business.impl.team.GetTeamsByUserImpl;
import com.replan.business.impl.teamMember.AddTeamMemberImpl;
import com.replan.business.impl.teamMember.GetTeamMembersByTeamImpl;
import com.replan.business.impl.teamMember.RemoveTeamMemberImpl;
import com.replan.business.usecases.team.CreateTeamUseCase;
import com.replan.business.usecases.team.DeleteTeamUseCase;
import com.replan.business.usecases.team.GetTeamsByOwnerUseCase;
import com.replan.business.usecases.team.GetTeamsByUserUseCase;
import com.replan.business.usecases.teamMember.AddTeamMemberUseCase;
import com.replan.business.usecases.teamMember.GetTeamMembersByTeamUseCase;
import com.replan.business.usecases.teamMember.RemoveTeamMemberUseCase;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.UserRepository;
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
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public CreateTeamUseCase createTeamUseCase(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository, UserRepository userRepository) {
        return new CreateTeamImpl(teamRepository, teamMemberRepository, userRepository);
    }

    @Bean
    public DeleteTeamUseCase deleteTeamUseCase(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository){
        return new DeleteTeamImpl(teamRepository, teamMemberRepository);
    };

    @Bean
    public RemoveTeamMemberUseCase removeTeamMemberUseCase(TeamMemberRepository teamMemberRepository, TeamRepository teamRepository) {
        return new RemoveTeamMemberImpl(teamMemberRepository, teamRepository);
    }

    @Bean
    public GetTeamsByUserUseCase getTeamsByUserUseCase(TeamMemberRepository teamMemberRepository, TeamRepository teamRepository) {
        return new GetTeamsByUserImpl(teamMemberRepository, teamRepository);
    }

    @Bean
    public AddTeamMemberUseCase addTeamMemberUseCase(
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            UserRepository userRepository
    ) {
        return new AddTeamMemberImpl(teamRepository, teamMemberRepository,userRepository);
    }

    @Bean
    public GetTeamsByOwnerUseCase getTeamsByOwnerUseCase(TeamRepository teamRepository) {
        return new GetTeamsByOwnerImpl(teamRepository);
    }

    @Bean
    public GetTeamMembersByTeamUseCase getTeamMembersByTeamUseCase(
            TeamMemberRepository teamMemberRepository,UserRepository userRepository
    ) {
        return new GetTeamMembersByTeamImpl(teamMemberRepository, userRepository);
    }
}