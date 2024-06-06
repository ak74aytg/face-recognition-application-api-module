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
    public ResponseEntity<String> addImages(Principal principal, @RequestParam("name") String name, @RequestParam(value = "image") MultipartFile file) throws Exception {
        ImageData imageData = new ImageData();
        imageData.setName(name);
        if (file == null || file.isEmpty()) {
            throw new IllegalAccessException("No image found");
        }
        String response = imageServices.saveImage(principal, imageData, file);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/user/images")
    public ResponseEntity<List<ImageData>> getImages(Principal principal) throws Exception {
        List<ImageData> images = imageServices.getImages(principal);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/user/images/delete/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable("imageId") String imageId, Principal principal) throws Exception {
        String message = imageServices.deleteImage(imageId, principal);
        return ResponseEntity.ok(message);
    }
}
