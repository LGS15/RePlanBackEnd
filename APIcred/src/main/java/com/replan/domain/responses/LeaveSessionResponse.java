package com.replan.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaveSessionResponse {
    public String sessionId;
    public String message;
    public Boolean success;
}
