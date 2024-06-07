package com.backend.controller;

import com.backend.models.User;
import com.backend.request.ImageRequest;
import com.backend.service.PublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    PublicService publicService;

    @PostMapping("send/image")
    public List<User> sendImage(@RequestParam("location") String location,
                                @RequestParam("image") MultipartFile file) throws Exception{
        ImageRequest imageRequest = new ImageRequest();
        imageRequest.setLocation(location);
        return publicService.verifyImage(file, imageRequest);
    }
}
