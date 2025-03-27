package com.replan.controller;

import com.replan.business.usecases.teamMember.AddTeamMemberUseCase;
import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.responses.AddTeamMemberResponse;
import com.replan.domain.responses.CreateTeamResponse;
import com.replan.domain.requests.CreateTeamRequest;
import com.replan.business.usecases.team.CreateTeamUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/teams")
public class TeamController {

    //Added Comment For Test
    private final CreateTeamUseCase createTeamUseCase;
    private final AddTeamMemberUseCase addTeamMemberUseCase;

    public TeamController(CreateTeamUseCase createTeamUseCase,AddTeamMemberUseCase addTeamMemberUseCase) {
        this.createTeamUseCase = createTeamUseCase;
        this.addTeamMemberUseCase = addTeamMemberUseCase;
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
}
