package com.replan.domain.requests;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserRequest {
    private String email;
    private String password;
}