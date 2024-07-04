package com.backend.service;

import com.backend.configuration.NotificationBuilder;
import com.backend.models.User;
import com.backend.repository.UserRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class NotificationService {

    @Autowired
    private UserRepository userRepository;

    // Initialize Firebase app
    @Autowired
    public NotificationService() throws IOException {
        String privateKey = System.getenv("FCM_PRIVATE_KEY");
        String clientEmail = System.getenv("FCM_CLIENT_EMAIL");
        String projectId = System.getenv("FCM_PROJECT_ID");
        String privateKeyId = System.getenv("FCM_PRIVATE_KEY_ID");
        String clientId = System.getenv("FCM_CLIENT_ID");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream((
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
                        .getBytes())))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }

    public void sendNotification(Integer pincode, String imageUrl, String message) throws IOException {
        Set<User> users = new HashSet<>();
        for (int i = pincode - 6; i < pincode + 7; i++) {
            users.addAll(userRepository.findByPincode(i));
        }

        Map<String , String> notificationMessage = new HashMap<>();
        notificationMessage.put("notifiation_id", UUID.randomUUID().toString());
        notificationMessage.put("title", "New person identified");
        notificationMessage.put("message", message);
        notificationMessage.put("body", "{\"ImageUrl\": \"" + imageUrl + "\"}");

        List<String> deviceTokens = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        for(User user : users){
            if(user.getToken()!=null && !user.getToken().isEmpty()){
                deviceTokens.add(user.getToken());
            }
            userList.add(user);
            user.getUserNotifications().add(notificationMessage);
        }


        if (!deviceTokens.isEmpty()) {
            sendPushNotification(deviceTokens, message, imageUrl);
            userRepository.saveAll(userList);
        }


    }

    private void sendPushNotification(List<String> deviceTokens, String messageBody, String imageUrl) {
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .putData("channelId", "default")
                    .putData("scopeKey", "@ayushbahuguna1122/App")
                    .putData("experienceId", "@ayushbahuguna1122/App")
                    .setNotification(NotificationBuilder.createNotification(
                            "New person identified", // Title
                            messageBody,             // Body
                            imageUrl                 // Image URL
                    ))
                    .addAllTokens(deviceTokens)
                    .build();

            FirebaseMessaging.getInstance().sendMulticast(message);

        } catch (Exception e) {
            System.out.println("Error sending push notifications: " + e.getMessage());
        }
    }
}
