package com.backend.service;

import com.backend.models.User;
import com.backend.repository.ImageDataRepository;
import com.backend.repository.UserRepository;
import com.backend.request.ImageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PublicService {

    @Autowired
    ImageDataRepository imageRepository;
    @Autowired
    UserRepository userRepository;

    public List<User> verifyImage(MultipartFile image, ImageRequest imageRequest) throws Exception{
        String loc = imageRequest.getLocation();
        List<User> users = userRepository.findByLocation(loc);
        return users;
    }
}
