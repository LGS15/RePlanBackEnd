package com.replan.domain.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewSessionRequest {
    private String teamId;
    private String videoUrl;
    private String title;
    private String description;

}
