package com.replan.practice;

import com.replan.business.impl.practice.GetPopularCombinationsImpl;
import com.replan.domain.objects.PracticeFocus;
import com.replan.domain.objects.PracticeType;
import com.replan.domain.responses.PopularCombinationResponse;
import com.replan.domain.responses.PopularCombinationsResponse;
import com.replan.persistance.PracticePlanRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class GetPopularCombinationsImplTest {

    @Mock
    private PracticePlanRequestRepository requestRepository;
    @InjectMocks
    private GetPopularCombinationsImpl subject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private PracticePlanRequestRepository.FocusCombinationStats mockStats(String f1, String f2, String f3, long cnt) {
        return new PracticePlanRequestRepository.FocusCombinationStats() {
            @Override public String getFocusOne() { return f1; }
            @Override public String getFocusTwo() { return f2; }
            @Override public String getFocusThree() { return f3; }
            @Override public Long getCnt() { return cnt; }
        };
    }

    @Test
    void shouldReturnPopularCombinations() {
        when(requestRepository.findMostPopularCombination(PracticeType.INDIVIDUAL.name()))
                .thenReturn(mockStats("AIM_TRAINING", "GAME_SENSE", null, 5));
        when(requestRepository.findMostPopularCombination(PracticeType.TEAM.name()))
                .thenReturn(mockStats("TEAM_COORDINATION", null, null, 3));

        PopularCombinationsResponse resp = subject.getPopularCombinations();

        assertThat(resp.getIndividual().getPracticeType()).isEqualTo(PracticeType.INDIVIDUAL);
        assertThat(resp.getIndividual().getFocuses()).containsExactly(PracticeFocus.AIM_TRAINING, PracticeFocus.GAME_SENSE);
        assertThat(resp.getIndividual().getCount()).isEqualTo(5);

        assertThat(resp.getTeam().getPracticeType()).isEqualTo(PracticeType.TEAM);
        assertThat(resp.getTeam().getFocuses()).containsExactly(PracticeFocus.TEAM_COORDINATION);
        assertThat(resp.getTeam().getCount()).isEqualTo(3);
    }
}