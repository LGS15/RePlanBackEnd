package com.replan.domain.responses;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserResponse {
    private String userId;
    private String username;
    private String email;
    private String token;

}
