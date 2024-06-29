package com.backend.service;
import com.backend.models.User;
import com.backend.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class NotificationService {

    @Autowired
    private UserRepository userRepository;

    public void sendNotification(String location, Integer pincode, String message) {
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
            Notification notification = Notification.builder()
                    .setTitle("New Image")
                    .setBody(messageBody)
                    .build();

            Message message = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(notification)
                    .putData("message", messageBody)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
