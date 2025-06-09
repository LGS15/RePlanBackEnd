package com.replan.controller;

import com.replan.business.usecases.reviewSession.*;
import com.replan.domain.requests.CreateReviewSessionRequest;
import com.replan.domain.requests.EndSessionRequest;
import com.replan.domain.requests.JoinSessionRequest;
import com.replan.domain.requests.LeaveSessionRequest;
import com.replan.domain.responses.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review-sessions")
@AllArgsConstructor
public class ReviewSessionController {

    private final CreateReviewSessionUseCase createReviewSessionUseCase;
    private final JoinReviewSessionUseCase joinReviewSessionUseCase;
    private final LeaveReviewSessionUseCase leaveReviewSessionUseCase;
    private final GetActiveSessionsUseCase getActiveSessionsUseCase;
    private final EndReviewSessionUseCase endReviewSessionUseCase;

    @PostMapping
    public ResponseEntity<CreateReviewSessionResponse> createSession(@RequestBody CreateReviewSessionRequest request) {
        CreateReviewSessionResponse response = createReviewSessionUseCase.createSession(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/join")
    public ResponseEntity<JoinSessionResponse> joinSession(@RequestBody JoinSessionRequest request) {
        JoinSessionResponse response = joinReviewSessionUseCase.joinSession(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/leave")
    public ResponseEntity<LeaveSessionResponse> leaveSession(@RequestBody LeaveSessionRequest request) {
        LeaveSessionResponse response = leaveReviewSessionUseCase.leaveSession(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/end")
    public ResponseEntity<EndSessionResponse> endSession(@RequestBody EndSessionRequest request) {
        EndSessionResponse response = endReviewSessionUseCase.endSession(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/team/{teamId}/active")
    public ResponseEntity<List<ReviewSessionResponse>> getActiveSessions(@PathVariable String teamId) {
        List<ReviewSessionResponse> sessions = getActiveSessionsUseCase.getActiveSessions(teamId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ReviewSessionResponse> getSession(@PathVariable String sessionId) {
        ReviewSessionResponse session = getActiveSessionsUseCase.getSessionById(sessionId);
        return ResponseEntity.ok(session);
    }
}
