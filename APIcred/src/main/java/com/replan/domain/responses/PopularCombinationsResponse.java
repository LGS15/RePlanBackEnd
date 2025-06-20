package com.replan.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PopularCombinationsResponse {
    private PopularCombinationResponse individual;
    private PopularCombinationResponse team;
}