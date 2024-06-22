package com.backend.service;

import com.backend.controller.NotificationController;
import com.backend.models.Notification;
import com.backend.models.User;
import com.backend.repository.NotificationRepository;
import com.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationController notificationController;

    public void sendNotification(String location,Integer pincode, String message) {
        List<User> users = userRepository.findByLocation(location);
        for(int i=pincode-3;i<pincode+4;i++) {
            users.addAll(userRepository.findByPincode(i));
        }
        for (User user : users) {
            notificationController.sendNotificationToUser(user.getEmail(), message);
            System.out.println(user.getEmail());
        }
        notificationController.sendNotificationToUser("admin", "new image uploaded in location "+location);
    }
}
