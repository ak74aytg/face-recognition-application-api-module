package com.backend.service;

import com.backend.models.User;
import com.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User registerUser(User user) {
        user.setId(UUID.randomUUID().toString());
        User savedUser = userRepository.save(user);
        return savedUser;
    }


    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if(user!=null) {
            return user;
        }
        throw new Exception("user does not exist with user email "+email);
    }
}
