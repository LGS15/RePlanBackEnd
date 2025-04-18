package com.replan.domain.responses;

import lombok.Data;
import lombok.AllArgsConstructor;


@Data
@AllArgsConstructor
public class CreateUserResponse {
    private String id;
    private String username;
    private String email;
}
