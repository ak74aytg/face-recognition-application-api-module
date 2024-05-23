package com.backend.response;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private HashMap<String, Object> payload;
    public AuthResponse(String token, HashMap<String, Object> payload) {
        super();
        this.token = token;
        this.payload = payload;
    }

    public AuthResponse() {
        super();
        // TODO Auto-generated constructor stub
    }
}
