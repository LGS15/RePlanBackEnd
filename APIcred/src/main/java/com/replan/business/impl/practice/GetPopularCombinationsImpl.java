package com.replan.business.impl.practice;

import com.replan.business.usecases.practice.GetPopularCombinationsUseCase;
import com.replan.domain.objects.PracticeFocus;
import com.replan.domain.objects.PracticeType;
import com.replan.domain.responses.PopularCombinationResponse;
import com.replan.domain.responses.PopularCombinationsResponse;
import com.replan.persistance.PracticePlanRequestRepository;

import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;

@Service
public class GetPopularCombinationsImpl implements GetPopularCombinationsUseCase {

    private final PracticePlanRequestRepository requestRepository;

    public GetPopularCombinationsImpl(PracticePlanRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public PopularCombinationsResponse getPopularCombinations() {
        Pageable limitOne = PageRequest.of(0, 1);
        PopularCombinationResponse individual = map(PracticeType.INDIVIDUAL,
                requestRepository.findMostPopularCombination(PracticeType.INDIVIDUAL, limitOne)
                        .stream().findFirst().orElse(null));
        PopularCombinationResponse team = map(PracticeType.TEAM,
                requestRepository.findMostPopularCombination(PracticeType.TEAM, limitOne)
                        .stream().findFirst().orElse(null));
        return new PopularCombinationsResponse(individual, team);
    }

    private PopularCombinationResponse map(PracticeType type,
                                           PracticePlanRequestRepository.FocusCombinationStats stats) {
        if (stats == null) {
            return new PopularCombinationResponse(type, List.of(), 0);
        }
        List<PracticeFocus> focuses = new ArrayList<>();
        if (stats.getFocusOne() != null) {
            focuses.add(PracticeFocus.valueOf(stats.getFocusOne()));
        }
        if (stats.getFocusTwo() != null) {
            focuses.add(PracticeFocus.valueOf(stats.getFocusTwo()));
        }
        if (stats.getFocusThree() != null) {
            focuses.add(PracticeFocus.valueOf(stats.getFocusThree()));
        }
        return new PopularCombinationResponse(type, focuses, stats.getCnt());
    }
}