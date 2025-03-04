package com.example.CapstoneFinalProjectBE.payload.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtResponse {

    private String email;
    private Long id;
    private boolean isAdmin;
    private String token;
    private String type = "Bearer ";

    public JwtResponse(String email, Long id, boolean isAdmin, String token) {
        this.email = email;
        this.id = id;
        this.isAdmin = isAdmin;
        this.token = token;
    }

}
