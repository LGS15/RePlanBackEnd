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

    // Keept only for backward compatibility with existing constructor
    public CreateUserResponse(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.token = null;
    }
}
