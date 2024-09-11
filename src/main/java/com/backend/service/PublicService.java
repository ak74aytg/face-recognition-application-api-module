package com.backend.service;

import com.backend.models.ImageData;
import com.backend.models.User;
import com.backend.repository.ImageDataRepository;
import com.backend.repository.UserRepository;
import com.backend.request.ImageRequest;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        notificationService.sendNotification(pin, imageUrl , "New person found in "+loc);
        try {
            Path tempFile = Files.createTempFile("upload-", image.getOriginalFilename());
            image.transferTo(tempFile.toFile());
            RestTemplate restTemplate = new RestTemplate();
            String flaskApiUrl = "https://facemodelapi.onrender.com/recognize";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new FileSystemResource(tempFile.toFile()));
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(flaskApiUrl, requestEntity, String.class);
            Files.delete(tempFile);
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
                Map<String, String> results = (Map<String, String>) responseMap.get("results");
                Set<String> keySet = results.keySet();
                String[] keysArray = keySet.toArray(new String[0]);
                for (String imageId: keysArray
                ) {
                    List<ImageData> imageDataList = imageRepository.findByImageUrlContains(imageId);
                    for (ImageData imageData : imageDataList) {
                        notificationService.sendNotification(imageData.getUser(), imageUrl, "The person you are looking for found in "+loc);
                    }
                }
                return "Thank You for helping us!";
            } else {
                return "Cannot identify the person in the picture!";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "No person found in the image!";
        }
    }

}
