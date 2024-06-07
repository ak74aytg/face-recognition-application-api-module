package com.backend.service;

import com.backend.models.User;
import com.backend.repository.UserRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Service
public class AdminService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    Cloudinary cloudinary;




    public String Home() throws Exception{
        return "hello admin";
    }

    public String DeleteUsers(String userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow();
        String url  = user.getProfile_url();
        int lastDotIndex = url.lastIndexOf('.');
        int lastSlashIndex = url.lastIndexOf('/');
        String imageName = url.substring(lastSlashIndex + 1, lastDotIndex);

        try {
            ApiResponse apiResponse = cloudinary.api()
                    .deleteResources(Collections.singletonList("saved-images/"+imageName),
                            ObjectUtils.asMap("type", "upload", "resource_type", "image"));
            System.out.println(apiResponse);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userRepository.deleteById(userId);
        return "user deleted successfully";
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
