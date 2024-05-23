package com.backend.controller;

import com.backend.models.User;
import com.backend.request.LoginRequest;
import com.backend.response.AuthResponse;
import com.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/auth/")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestParam("user") String userJson, @RequestParam("profile-image") MultipartFile file) throws Exception {
    	ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(userJson, User.class);
    	AuthResponse response = authService.RegisterUser(user, file);
        String status = (String) response.getPayload().get("status");
        if(status.equals("failed")) {
        	return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }


    @PostMapping("signin")
    public ResponseEntity<AuthResponse> loginUserHandler(@RequestBody LoginRequest loginRequest) throws Exception {
    	AuthResponse response = authService.loginUser(loginRequest);
    	return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        
    }


    
}
