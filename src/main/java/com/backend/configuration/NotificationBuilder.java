package com.backend.configuration;

import com.google.firebase.messaging.Notification;

public class NotificationBuilder {
    public static Notification createNotification(String title, String body, String imageUrl) {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .setImage(imageUrl)
                .build();
    }
}
