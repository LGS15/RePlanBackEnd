package com.replan.business.impl.team;

import com.replan.business.mapper.TeamMapper;
import com.replan.business.usecases.team.CreateTeamUseCase;
import com.replan.domain.objects.Role;
import com.replan.domain.objects.Team;
import com.replan.domain.requests.CreateTeamRequest;
import com.replan.domain.responses.CreateTeamResponse;
import com.replan.persistance.TeamMemberRepository;
import com.replan.persistance.TeamRepository;
import com.replan.persistance.UserRepository;
import com.replan.persistance.entity.TeamEntity;
import com.replan.persistance.entity.TeamMemberEntity;
import com.replan.persistance.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CreateTeamImpl  implements CreateTeamUseCase {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    public CreateTeamImpl(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
    }

   @Override
   @Transactional
    public CreateTeamResponse createTeam(CreateTeamRequest request) {
        if (request.getTeamName() == null || request.getTeamName().isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be empty");
        }
        if (request.getGameName() == null || request.getGameName().isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be empty");
        }
        if (request.getOwnerId() == null || request.getOwnerId().isEmpty()) {
            throw new IllegalArgumentException("Owner id cannot be empty");
        }

       Optional<UserEntity> ownerEntity = userRepository.findById(UUID.fromString(request.getOwnerId()));
        if (ownerEntity.isEmpty()) {
            throw new IllegalArgumentException("Owner does not exist");
        }


        TeamEntity toSave = TeamMapper.toEntity(request);
        TeamEntity savedTeam = teamRepository.save(toSave);

        TeamMemberEntity ownerMember = new TeamMemberEntity();
        ownerMember.setTeamId(savedTeam.getId());
        ownerMember.setUserId(request.getOwnerId());
        ownerMember.setRole(Role.OWNER);

        teamMemberRepository.save(ownerMember);

        return TeamMapper.toCreateResponse(savedTeam);
   }
}
