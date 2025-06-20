package com.replan.controller;

import com.replan.business.usecases.practice.GetPopularCombinationsUseCase;
import com.replan.business.usecases.practice.GetTeamPracticeHistoryUseCase;
import com.replan.business.usecases.practice.GetUserPracticeHistoryUseCase;
import com.replan.business.usecases.practice.PracticeTimeCalculatorUseCase;
import com.replan.domain.objects.PracticeFocusInfo;
import com.replan.domain.requests.CalculatePracticeRequest;
import com.replan.domain.responses.CalculatePracticeResponse;
import com.replan.domain.responses.PopularCombinationsResponse;
import com.replan.persistance.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/practice-calculator")
@AllArgsConstructor
public class PracticeCalculatorController {

    private final PracticeTimeCalculatorUseCase calculatorUseCase;
    private final GetUserPracticeHistoryUseCase userHistoryUseCase;
    private final GetTeamPracticeHistoryUseCase teamHistoryUseCase;
    private final GetPopularCombinationsUseCase popularCombinationsUseCase;

    @PostMapping("/calculate")
    public ResponseEntity<CalculatePracticeResponse> calculatePlan(
            @RequestBody CalculatePracticeRequest request
    ) {
        CalculatePracticeResponse response = calculatorUseCase.calculatePracticeAllocation(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/focuses")
    public ResponseEntity<List<PracticeFocusInfo>> getAvailableFocuses() {
        List<PracticeFocusInfo> focuses = calculatorUseCase.getAvailableFocuses();
        return ResponseEntity.ok(focuses);
    }

    @GetMapping("/history")
    public ResponseEntity<List<CalculatePracticeResponse>> getCurrentUserPracticeHistory(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        String currentUserId = getCurrentUserId();
        List<CalculatePracticeResponse> history = userHistoryUseCase.getUserPracticeHistory(currentUserId, page, limit);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<CalculatePracticeResponse>> getUserPracticeHistory(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        List<CalculatePracticeResponse> history = userHistoryUseCase.getUserPracticeHistory(userId, page, limit);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/team/{teamId}/history")
    public ResponseEntity<List<CalculatePracticeResponse>> getTeamPracticeHistory(
            @PathVariable String teamId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        List<CalculatePracticeResponse> history = teamHistoryUseCase.getTeamPracticeHistory(teamId, page, limit);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/popular")
    public ResponseEntity<PopularCombinationsResponse> getPopularCombinations() {
        PopularCombinationsResponse response = popularCombinationsUseCase.getPopularCombinations();
        return ResponseEntity.ok(response);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity)) {
            throw new AccessDeniedException("User not authenticated");
        }
        return ((UserEntity) authentication.getPrincipal()).getId().toString();
    }
}