package com.backend.service;

import com.backend.models.ImageData;
import com.backend.models.User;
import com.backend.repository.ImageDataRepository;
import com.backend.repository.UserRepository;
import com.backend.request.ImageRequest;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.*;

import java.nio.file.Files;
import java.nio.file.Path;
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




    public String verifyImage(MultipartFile image, ImageRequest imageRequest) throws Exception {
        String loc = imageRequest.getLocation();
        Integer pin = imageRequest.getPin();
        byte[] imageBytes = image.getBytes();
        Map options = ObjectUtils.asMap(
                "folder", "temporary-images/"
        );
        String imageUrl = (String) cloudinary.uploader().upload(imageBytes, options).get("secure_url");

        try {
            RestTemplate restTemplate = new RestTemplateBuilder()
                    .setConnectTimeout(Duration.ofSeconds(100))
                    .setReadTimeout(Duration.ofSeconds(100))
                    .build();

            String flaskApiUrl = "http://43.204.142.232:80/upload";  // Assuming the Flask API URL
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Build the JSON payload
            Map<String, String> body = new HashMap<>();
            body.put("image_url", imageUrl);
            System.out.println(imageUrl);
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

            // Make the API call to the Flask service
            ResponseEntity<String> response = restTemplate.postForEntity(flaskApiUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
                String result = (String) responseMap.get("result");

                // Handle successful matches
                if (result != null && !result.equals("No match found.")) {
                    // Notify all users with matching images
                    List<ImageData> imageDataList = imageRepository.findByImageUrlContains(result);
                    for (ImageData imageData : imageDataList) {
                        notificationService.sendNotification(imageData.getUser(), imageUrl, "The person you are looking for was found in " + loc);
                    }
                    notificationService.sendNotification(pin, imageUrl, "New person found in " + loc);
                    return "Thank you for helping us!";
                } else {
                    return "Cannot identify the person in the picture!";
                }
            } else {
                return "Cannot identify the person in the picture!";
            }
        }catch (ResourceAccessException e) {
            // Handle timeout and connection issues
            e.printStackTrace();
            return "Request timed out. Please try again later.";
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            // Handle HTTP errors
            e.printStackTrace();
            return "An error occurred while processing your request. Please try again later.";
        } catch (Exception e) {
            // Handle other unexpected exceptions
            e.printStackTrace();
            return "An unexpected error occurred. Please try again later.";
        }
    }

}
