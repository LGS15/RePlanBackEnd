package com.replan.controller;

import com.replan.business.usecases.team.GetTeamsByOwnerUseCase;
import com.replan.business.usecases.teamMember.AddTeamMemberUseCase;
import com.replan.business.usecases.teamMember.GetTeamMembersByTeamUseCase;
import com.replan.business.usecases.teamMember.RemoveTeamMemberUseCase;
import com.replan.domain.requests.AddTeamMemberRequest;
import com.replan.domain.requests.GetTeamMembersByTeamRequest;
import com.replan.domain.requests.RemoveTeamMemberRequest;
import com.replan.domain.responses.*;
import com.replan.domain.requests.CreateTeamRequest;
import com.replan.business.usecases.team.CreateTeamUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/teams")

@AllArgsConstructor
public class TeamController {

    //Added Comment For Test
    private final CreateTeamUseCase createTeamUseCase;
    private final AddTeamMemberUseCase addTeamMemberUseCase;
    private final GetTeamsByOwnerUseCase getTeamsByOwnerUseCase;
    private final GetTeamMembersByTeamUseCase getTeamMembersByTeamUseCase;
    private final RemoveTeamMemberUseCase removeTeamMemberUseCase;


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

    @GetMapping("/{teamId}/members")
    public ResponseEntity<GetTeamMembersByTeamResponse> getTeamMembers(@PathVariable String teamId){
        GetTeamMembersByTeamRequest req = new GetTeamMembersByTeamRequest(teamId);

        GetTeamMembersByTeamResponse resp = getTeamMembersByTeamUseCase.getTeamMembers(req);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<RemoveTeamMemberResponse> removeTeamMember(@PathVariable String teamId, @PathVariable String userId ){
        RemoveTeamMemberRequest request= new RemoveTeamMemberRequest(teamId, userId);
        RemoveTeamMemberResponse response = removeTeamMemberUseCase.removeTeamMember(request);
        return ResponseEntity.ok(response);
    }

}
