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
@RequestMapping("/user")
public class ImageController {
    @Autowired
    ImageServices imageServices;


    @PostMapping("/image")
    public ResponseEntity<String> addImages(Principal principal,
                                            @RequestParam("guardian") String guardian,
                                            @RequestParam("location") String location,
                                            @RequestParam("mobile") Long mobile,
                                            @RequestParam("image") MultipartFile file,
                                            @RequestParam("pincode") Integer pincode,
                                            @RequestParam("missing-data") String missing_data,
                                            @RequestParam("gender") String gender,
                                            @RequestParam("station") String station,
                                            @RequestParam("state") String state)
            throws Exception {
        ImageData imageData = new ImageData();
        imageData.setGuardian(guardian);
        imageData.setLocation(location);
        imageData.setMobile(mobile);
        imageData.setPincode(pincode);
        imageData.setMissingData(missing_data);
        imageData.setGender(gender);
        imageData.setStation(station);
        imageData.setState(state);
        imageData.setStatus("unsolved");
        if (file == null || file.isEmpty()) {
            throw new IllegalAccessException("No image found");
        }
        String response = imageServices.saveImage(principal, imageData, file);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/images")
    public ResponseEntity<List<ImageData>> getImages(Principal principal) throws Exception {
        List<ImageData> images = imageServices.getImages(principal);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/images/delete/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable("imageId") String imageId, Principal principal) throws Exception {
        String message = imageServices.deleteImage(imageId, principal);
        return ResponseEntity.ok(message);
    }
}
