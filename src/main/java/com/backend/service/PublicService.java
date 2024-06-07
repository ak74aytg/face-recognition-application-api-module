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
    @Autowired
    NotificationService notificationService;

    public List<User> verifyImage(MultipartFile image, ImageRequest imageRequest) throws Exception{
        String loc = imageRequest.getLocation();
        notificationService.sendNotification(loc, "New image uploaded in your residential location!");
        return userRepository.findByLocation(loc);
    }
}
