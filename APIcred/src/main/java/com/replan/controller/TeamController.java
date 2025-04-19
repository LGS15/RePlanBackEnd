package com.replan.controller;

import com.replan.business.usecases.team.GetTeamsByOwnerUseCase;
import com.replan.business.usecases.teamMember.AddTeamMemberUseCase;
import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.responses.AddTeamMemberResponse;
import com.replan.domain.responses.CreateTeamResponse;
import com.replan.domain.requests.CreateTeamRequest;
import com.replan.business.usecases.team.CreateTeamUseCase;
import com.replan.domain.responses.TeamResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/teams")
@CrossOrigin(origins="http://localhost:5173")
public class TeamController {

    //Added Comment For Test
    private final CreateTeamUseCase createTeamUseCase;
    private final AddTeamMemberUseCase addTeamMemberUseCase;
    private final GetTeamsByOwnerUseCase getTeamsByOwnerUseCase;

    public TeamController(CreateTeamUseCase createTeamUseCase, AddTeamMemberUseCase addTeamMemberUseCase, GetTeamsByOwnerUseCase getTeamsByOwnerUseCase) {
        this.createTeamUseCase = createTeamUseCase;
        this.addTeamMemberUseCase = addTeamMemberUseCase;
        this.getTeamsByOwnerUseCase = getTeamsByOwnerUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateTeamResponse> createTeam(@RequestBody CreateTeamRequest request){
        CreateTeamResponse createTeamResponse = createTeamUseCase.createTeam(request);
        return new ResponseEntity<>(createTeamResponse, HttpStatus.CREATED);
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<AddTeamMemberResponse> addTeamMember(
            @PathVariable String teamId,
            @RequestBody AddTeamMemberRequest request){
        request.setTeamId(teamId);
        AddTeamMemberResponse response = addTeamMemberUseCase.addTeamMember(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping ("/owner/{ownerId}")
    public ResponseEntity<List<TeamResponse>>getTeamsByOwner(@PathVariable String ownerId){
        List<TeamResponse> teams = getTeamsByOwnerUseCase.getTeamsByOwner(ownerId);
        return ResponseEntity.ok(teams);
    }

    //This is where I should add the new endpoint
}
