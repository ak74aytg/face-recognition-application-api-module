package com.backend.service;

import com.backend.models.User;
import com.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public String editLocation(Principal principal, String location, Integer pincode){
        User user = userRepository.findByEmail(principal.getName());
        if(location!=null) user.setLocation(location);
        if(pincode!=null) user.setPincode(pincode);
        userRepository.save(user);
        return "location updated successfully";
    }

    public User getCurrentUser(Principal principal) {
        return userRepository.findByEmail(principal.getName());
    }

    public String editToken(String token, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        user.setToken(token);
        userRepository.save(user);
        return "Token updated successfully";
    }
}
