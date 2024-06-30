package com.backend.service;
import com.backend.models.User;
import com.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class NotificationService {
    private static final String FCM_PROJECT_NAME = "notification-e33f2";


    @Autowired
    private UserRepository userRepository;

    public void sendNotification(String location, Integer pincode, String message) throws IOException, FirebaseMessagingException {
        Set<User> users = new HashSet<>(userRepository.findByLocation(location));
        for (int i = pincode - 3; i < pincode + 4; i++) {
            users.addAll(userRepository.findByPincode(i));
        }

        for (User user : users) {
            String deviceToken = user.getToken();
            if (deviceToken != null && !deviceToken.isEmpty()) {
                sendPushNotification(deviceToken, message);
            }
        }
    }

    private void sendPushNotification(String deviceToken, String messageBody) {
        try {
            String firebaseAccessToken = getAccessToken();
            String response = sendFcmMessage(firebaseAccessToken, deviceToken, messageBody);
            System.out.println(response +" res");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String getAccessToken() throws IOException {
        try (FileInputStream serviceAccountStream = new FileInputStream("src/main/resources/fcm.json")) {
            GoogleCredentials googleCredentials = ServiceAccountCredentials.fromStream(serviceAccountStream)
                    .createScoped("https://www.googleapis.com/auth/cloud-platform");
            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (Exception e) {
            throw new IOException("Error getting access token: " + e.getMessage(), e);
        }
    }


    private String sendFcmMessage(String firebaseAccessToken, String DEVICE_TOKEN, String messages) throws IOException {
        Map<String, Object> message = new HashMap<>();
        Map<String, Object> messageBody = new HashMap<>();
        Map<String, String> data = new HashMap<>();
        data.put("channelId", "default");
        data.put("title", "New person identified");
        data.put("message", messages);
        data.put("body", "{\"ImageUrl\": \"lkfjdslijflf jewijliglknrew\"}");
        data.put("scopeKey", "@ayushbahuguna1122/App");
        data.put("experienceId", "@ayushbahuguna1122/App");

        messageBody.put("token", DEVICE_TOKEN);
        messageBody.put("data", data);
        message.put("message", messageBody);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = objectMapper.writeValueAsString(message);

        String url = "https://fcm.googleapis.com/v1/projects/" + FCM_PROJECT_NAME + "/messages:send";
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization", "Bearer " + firebaseAccessToken);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(jsonMessage));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return new ObjectMapper().readTree(response.getEntity().getContent()).toString();
        }
    }
}
