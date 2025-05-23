package com.replan.domain.requests;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private String email;
    private String username;
    private String password;
}