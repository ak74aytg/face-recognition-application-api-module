package com.backend.controller;

import com.backend.models.User;
import com.backend.repository.UserRepository;
import com.backend.request.LoginRequest;
import com.backend.response.AuthResponse;
import com.backend.security.JwtProvider;
import com.backend.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth/")
public class AuthController {

    @Autowired
    private UserRepository userDao;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
        User isExist = userDao.findByEmail(user.getEmail());
        if(isExist!=null) {
            AuthResponse res = new AuthResponse(null, "User already exist");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userDao.save(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
        String token = JwtProvider.generateToken(authentication);
        AuthResponse res = new AuthResponse(token, "Registered successfully");
        return ResponseEntity.ok(res);
    }


    @PostMapping("signin")
    public ResponseEntity<AuthResponse> loginUserHandler(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticate(loginRequest.getEmail(), loginRequest.getPassword());
            String token = JwtProvider.generateToken(authentication);
            AuthResponse res = new AuthResponse(token, "login successfull");
            return ResponseEntity.ok(res);
    }


    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        if(userDetails==null) {
            throw new BadCredentialsException("invalid username");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("wrong password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword());
    }
}
