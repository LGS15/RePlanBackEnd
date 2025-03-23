package com.replan.controller;

import com.replan.domain.responses.CreateTeamResponse;
import com.replan.domain.requests.CreateTeamRequest;
import com.replan.business.usecases.team.CreateTeamUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/teams")
public class TeamController {

    private final CreateTeamUseCase createTeamUseCase;

    public TeamController(CreateTeamUseCase createTeamUseCase) {
        this.createTeamUseCase = createTeamUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateTeamResponse> createTeam(@RequestBody CreateTeamRequest request){
        CreateTeamResponse createTeamResponse = createTeamUseCase.createTeam(request);
        return new ResponseEntity<>(createTeamResponse, HttpStatus.CREATED);
    }
}
