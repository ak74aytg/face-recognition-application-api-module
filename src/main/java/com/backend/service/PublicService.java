package com.backend.service;

import com.backend.models.User;
import com.backend.repository.ImageDataRepository;
import com.backend.repository.UserRepository;
import com.backend.request.ImageRequest;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class PublicService {

    @Autowired
    ImageDataRepository imageRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    NotificationService notificationService;
    @Autowired
    Cloudinary cloudinary;

    public String verifyImage(MultipartFile image, ImageRequest imageRequest) throws Exception{
        String loc = imageRequest.getLocation();
        Integer pin = imageRequest.getPin();
        byte[] imageBytes = image.getBytes();
        Map options = ObjectUtils.asMap(
                "folder", "temporary-images/"
        );
        String imageUrl = (String) cloudinary.uploader().upload(imageBytes, options).get("secure_url");
        notificationService.sendNotification(pin, imageUrl , "New person found in "+loc);
        return "image sent successfully";
    }
}
