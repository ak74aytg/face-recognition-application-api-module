package com.backend.service;

import com.backend.configuration.NotificationBuilder;
import com.backend.models.User;
import com.backend.repository.UserRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

        List<Integer> pincodes = getPinQueue(pincode);

        for (Integer integer : pincodes) {
            users.addAll(userRepository.findByPincode(integer));
        }

        Map<String, String> notificationMessage = new HashMap<>();
        notificationMessage.put("notifiation_id", UUID.randomUUID().toString());
        notificationMessage.put("title", "New person identified");
        notificationMessage.put("message", message);
        notificationMessage.put("body", "{\"ImageUrl\": \"" + imageUrl + "\"}");

        // Add current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        notificationMessage.put("timestamp", now.format(formatter));

        List<String> deviceTokens = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        for (User user : users) {
            if (user.getToken() != null && !user.getToken().isEmpty()) {
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


    public void sendNotification(User user, String imageUrl, String message) throws IOException {


        Map<String, String> notificationMessage = new HashMap<>();
        notificationMessage.put("notifiation_id", UUID.randomUUID().toString());
        notificationMessage.put("title", "New person identified");
        notificationMessage.put("message", message);
        notificationMessage.put("body", "{\"ImageUrl\": \"" + imageUrl + "\"}");

        // Add current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        notificationMessage.put("timestamp", now.format(formatter));

        List<String> deviceTokens = new ArrayList<>();
        if (user.getToken() != null && !user.getToken().isEmpty()) {
            deviceTokens.add(user.getToken());
        }
        user.getUserNotifications().add(notificationMessage);

        if (!deviceTokens.isEmpty()) {
            sendPushNotification(deviceTokens, message, imageUrl);
            userRepository.save(user);
        }
    }

    private List<Integer> getPinQueue(Integer pincode) {
        List<User> usersWithPincode = userRepository.findAllPincodes();
        Set<Integer> pincodes = usersWithPincode.stream()
                .map(User::getPincode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Integer> uniquePincodes = new ArrayList<>(pincodes);
        Collections.sort(uniquePincodes);

        int index = getIdx(uniquePincodes, pincode);
        List<Integer> result = new ArrayList<>();

        // Get previous 5 pincodes
        for (int i = index - 5; i < index; i++) {
            if (i >= 0) {
                result.add(uniquePincodes.get(i));
            }
        }

        // Get the current pincode and next 5 pincodes
        for (int i = index; i <= index + 5 && i < uniquePincodes.size(); i++) {
            result.add(uniquePincodes.get(i));
        }

        return result;
    }


    private int getIdx(List<Integer> uniquePincodes, Integer pincode) {
        int start = 0;
        int end = uniquePincodes.size() - 1;

        while (start <= end) {
            int mid = start + (end - start) / 2;
            if (Objects.equals(uniquePincodes.get(mid), pincode)) {
                return mid;
            } else if (uniquePincodes.get(mid) > pincode) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
        }
        return start; // This returns the insertion point if the pincode is not found
    }


    private void sendPushNotification(List<String> deviceTokens, String messageBody, String imageUrl) {
        try {
            AndroidConfig androidConfig = AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                            .setTitle("New person identified")
                            .setBody(messageBody)
                            .setImage(imageUrl)
                            .build())
                    .build();

            MulticastMessage message = MulticastMessage.builder()
                    .putData("channelId", "default")
                    .putData("scopeKey", "@ayushbahuguna1122/App")
                    .putData("experienceId", "@ayushbahuguna1122/App")
                    .setAndroidConfig(androidConfig)
                    .addAllTokens(deviceTokens)
                    .build();

            FirebaseMessaging.getInstance().sendMulticast(message);
        } catch (Exception e) {
            System.out.println("Error sending push notifications: " + e.getMessage());
        }
    }
}
