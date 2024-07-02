package com.backend.service;

import com.backend.models.User;
import com.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public String editLocation(Principal principal, String location, Integer pincode){
        User user = userRepository.findByEmail(principal.getName());
        if(location!=null) user.setLocation(location);
        if(pincode!=null) user.setPincode(pincode);
        userRepository.save(user);
        return "location updated successfully";
    }

    public User getCurrentUser(Principal principal) {
        return userRepository.findByEmail(principal.getName());
    }

    public String editToken(String token, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        user.setToken(token);
        userRepository.save(user);
        return "Token updated successfully";
    }

    public List<Map<String, String>> getNotifications(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        return user.getUserNotifications();
    }

    public String deleteAllNotifications(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        user.setUserNotifications(new ArrayList<>());
        userRepository.save(user);
        return "Notifications deleted successfully";
    }
    public String deleteNotification(Principal principal, String notificationID){
        User user = userRepository.findByEmail(principal.getName());
        Map<String, String> deletedNotification = null;
        for(Map<String, String> notification : user.getUserNotifications()){
            if(notification.get("notifiation_id").equals(notificationID)){
                deletedNotification = notification;
                break;
            }
        }
        user.getUserNotifications().remove(deletedNotification);
        userRepository.save(user);
        return "Notification deleted successfully";
    }
}
