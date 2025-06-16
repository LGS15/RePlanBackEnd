package com.replan.reviewSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replan.business.usecases.reviewSession.*;
import com.replan.domain.requests.CreateReviewSessionRequest;
import com.replan.domain.requests.EndSessionRequest;
import com.replan.domain.requests.JoinSessionRequest;
import com.replan.domain.requests.LeaveSessionRequest;
import com.replan.domain.responses.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
@ActiveProfiles("test")
public class ReviewSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateReviewSessionUseCase createUseCase;
    @MockitoBean
    private JoinReviewSessionUseCase joinUseCase;
    @MockitoBean
    private LeaveReviewSessionUseCase leaveUseCase;
    @MockitoBean
    private GetActiveSessionsUseCase getUseCase;
    @MockitoBean
    private EndReviewSessionUseCase endUseCase;

    @BeforeEach
    void resetMocks() {
        reset(createUseCase, joinUseCase, leaveUseCase, getUseCase, endUseCase);
    }

    @Test
    void createSession_shouldReturnCreated() throws Exception {
        UUID sessionId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        CreateReviewSessionRequest request = new CreateReviewSessionRequest(
                teamId.toString(),
                "https://video.example.com/vid.mp4",
                "Session Title",
                "Description"
        );
        CreateReviewSessionResponse response = new CreateReviewSessionResponse(
                sessionId.toString(),
                teamId.toString(),
                request.getVideoUrl(),
                request.getTitle(),
                request.getDescription(),
                0L,
                false,
                "creator"
        );
        when(createUseCase.createSession(any(CreateReviewSessionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/review-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").value(sessionId.toString()))
                .andExpect(jsonPath("$.teamId").value(teamId.toString()))
                .andExpect(jsonPath("$.videoUrl").value(request.getVideoUrl()))
                .andExpect(jsonPath("$.title").value(request.getTitle()))
                .andExpect(jsonPath("$.description").value(request.getDescription()));
    }

    @Test
    void joinSession_shouldReturnOk() throws Exception {
        UUID sessionId = UUID.randomUUID();
        JoinSessionRequest request = new JoinSessionRequest(sessionId.toString());
        JoinSessionResponse response = new JoinSessionResponse(
                sessionId.toString(),
                "Joined",
                10L,
                true,
                3
        );
        when(joinUseCase.joinSession(any(JoinSessionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/review-sessions/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId.toString()))
                .andExpect(jsonPath("$.message").value("Joined"))
                .andExpect(jsonPath("$.currentTimestamp").value(10))
                .andExpect(jsonPath("$.isPlaying").value(true))
                .andExpect(jsonPath("$.activeParticipants").value(3));
    }

    @Test
    void leaveSession_shouldReturnOk() throws Exception {
        UUID sessionId = UUID.randomUUID();
        LeaveSessionRequest request = new LeaveSessionRequest(sessionId.toString());
        LeaveSessionResponse response = new LeaveSessionResponse(
                sessionId.toString(),
                "Left",
                true
        );
        when(leaveUseCase.leaveSession(any(LeaveSessionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/review-sessions/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId.toString()))
                .andExpect(jsonPath("$.message").value("Left"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void endSession_shouldReturnOk() throws Exception {
        UUID sessionId = UUID.randomUUID();
        EndSessionRequest request = new EndSessionRequest(sessionId.toString());
        EndSessionResponse response = new EndSessionResponse(
                sessionId.toString(),
                "Ended",
                true
        );
        when(endUseCase.endSession(any(EndSessionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/review-sessions/end")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId.toString()))
                .andExpect(jsonPath("$.message").value("Ended"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getActiveSessions_shouldReturnList() throws Exception {
        UUID sessionId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        ReviewSessionResponse resp = new ReviewSessionResponse(
                sessionId.toString(),
                teamId.toString(),
                "url",
                "title",
                "desc",
                0L,
                false,
                "creator",
                "ACTIVE",
                5
        );
        when(getUseCase.getActiveSessions(teamId.toString())).thenReturn(List.of(resp));

        mockMvc.perform(get("/review-sessions/team/{teamId}/active", teamId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionId").value(sessionId.toString()))
                .andExpect(jsonPath("$[0].teamId").value(teamId.toString()))
                .andExpect(jsonPath("$[0].title").value("title"));
    }

    @Test
    void getSession_shouldReturnSession() throws Exception {
        UUID sessionId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        ReviewSessionResponse resp = new ReviewSessionResponse(
                sessionId.toString(),
                teamId.toString(),
                "url",
                "title",
                "desc",
                0L,
                false,
                "creator",
                "ACTIVE",
                5
        );
        when(getUseCase.getSessionById(sessionId.toString())).thenReturn(resp);

        mockMvc.perform(get("/review-sessions/{sessionId}", sessionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId.toString()))
                .andExpect(jsonPath("$.teamId").value(teamId.toString()))
                .andExpect(jsonPath("$.title").value("title"));
    }
}