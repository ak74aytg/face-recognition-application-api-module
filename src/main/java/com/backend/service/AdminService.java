package com.backend.service;

import com.backend.models.User;
import com.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

@Service
public class AdminService {
    @Autowired
    UserRepository userRepository;

    @Value("${project.image}")
    private String UPLOAD_DIR;


    public String Home(Principal principal) throws Exception{
        User user = userRepository.findByEmail(principal.getName());
        if(user==null){
            throw new BadCredentialsException("you are not allowed to access this api!");
        }
        if(!user.getRole().equals("ADMIN")){
            throw new BadCredentialsException("no are not allowed to access this api!");
        }
        return "hello admin";
    }

    public String DeleteUsers(Principal principal, String userId) throws Exception {
        User admin = userRepository.findByEmail(principal.getName());
        if(!admin.getRole().equals("ADMIN")){
            throw new BadCredentialsException("no are not allowed to access this api!");
        }
        User user = userRepository.findById(userId).orElseThrow();
        String FILE_NAME = user.getProfile_url();
        Path path = Paths.get(UPLOAD_DIR+ File.separator+FILE_NAME);
        Files.deleteIfExists(path);
        userRepository.deleteById(userId);
        return "user deleted successfully";
    }
}
