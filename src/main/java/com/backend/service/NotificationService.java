package com.backend.service;
import com.backend.models.User;
import com.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.messaging.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class NotificationService {
    private static final String FCM_PROJECT_NAME = System.getenv("FCM_PROJECT_ID");


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
        String privateKey = System.getenv("FCM_PRIVATE_KEY");
        String clientEmail = System.getenv("FCM_CLIENT_EMAIL");
        String projectId = System.getenv("FCM_PROJECT_ID");
        String privateKeyId = System.getenv("FCM_PRIVATE_KEY_ID");
        String clientId = System.getenv("FCM_CLIENT_ID");

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ByteArrayInputStream((
                "{\n" +
                        "  \"type\": \"service_account\",\n" +
                        "  \"project_id\": \"" + projectId + "\",\n" +
                        "  \"private_key_id\": \"" + privateKeyId + "\",\n" +
                        "  \"private_key\": \"" + privateKey + "\",\n" +
                        "  \"client_email\": \"" + clientEmail + "\",\n" +
                        "  \"client_id\": \"" + clientId + "\",\n" +
                        "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                        "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                        "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                        "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/" + clientEmail + "\",\n" +
                        "  \"universe_domain\": \"googleapis.com\"\n" +
                        "}")
                .getBytes())).createScoped("https://www.googleapis.com/auth/cloud-platform");
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
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
