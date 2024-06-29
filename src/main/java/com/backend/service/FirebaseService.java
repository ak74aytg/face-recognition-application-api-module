package com.backend.service;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

@Service
public class FirebaseService {

    private static final String SCOPES = "https://www.googleapis.com/auth/cloud-platform";
    Resource resource = new ClassPathResource("fcm.json");

    public String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials;
        try (InputStream serviceAccount = resource.getInputStream()) {
            googleCredentials = GoogleCredentials.fromStream(serviceAccount)
                    .createScoped(Collections.singletonList(SCOPES));
            googleCredentials.refreshIfExpired();
        }

        return googleCredentials.getAccessToken().getTokenValue();
    }
}
