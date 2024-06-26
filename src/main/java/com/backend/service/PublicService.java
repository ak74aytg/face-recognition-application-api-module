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

    public void verifyImage(MultipartFile image, ImageRequest imageRequest) throws Exception{
        String loc = imageRequest.getLocation();
        Integer pin = imageRequest.getPin();
        notificationService.sendNotification(loc, pin, "New image uploaded in your nearby location!");
    }
}
