package com.backend.controller;

import com.backend.models.ImageData;
import com.backend.models.User;
import com.backend.response.AuthResponse;
import com.backend.service.ImageServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.Principal;
import java.util.List;

@RestController
public class ImageController {
    @Value("${project.image}")
    String path;
    @Autowired
    ImageServices imageServices;

    @GetMapping(value = "/images/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public void downloadImage(
            @PathVariable("imageName") String imageName,
            HttpServletResponse response
    ) throws IOException {
        String fullPath = path+ File.separator+imageName;
        InputStream resource = new FileInputStream(fullPath);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }


    @PostMapping("/add-image")
    public String addImages(Principal principal, @RequestParam("name") String name, @RequestParam(value = "image") MultipartFile file) throws Exception{
        ImageData imageData = new ImageData();
        imageData.setName(name);
        String response;
        if (file == null || file.isEmpty()) {
            throw new IllegalAccessException("no image found");
        }
        response = imageServices.SaveImages(principal, imageData, file);

        return response;
    }


    @GetMapping("/user/images")
    public List<ImageData> getImages(Principal principal) throws Exception{
        return imageServices.getImages(principal);
    }

    @GetMapping("/user/images/delete/{imageId}")
    public String deleteImage(@PathVariable("imageId") String imageId, Principal principal) throws Exception{
        return imageServices.deleteImage(imageId, principal);
    }
}
