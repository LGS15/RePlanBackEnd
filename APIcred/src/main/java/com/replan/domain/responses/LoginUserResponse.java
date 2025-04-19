package com.replan.domain.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserResponse {
    private String userId;
    private String username;
    private String email;
    private String token;

    //Same as the CreateUserResponse
    public LoginUserResponse(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.token = null;
    }
}
