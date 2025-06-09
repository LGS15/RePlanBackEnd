package com.replan.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndSessionResponse {
    public String sessionId;
    public String message;
    public Boolean success;
}
