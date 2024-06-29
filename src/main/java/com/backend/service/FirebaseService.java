package com.backend.service;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

@Service
public class FirebaseService {

    private static final String SCOPES = "https://www.googleapis.com/auth/cloud-platform";
    private static final String SERVICE_ACCOUNT_FILE = "src/main/resources/fcm.json";

    public String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials;
        try (FileInputStream serviceAccountStream = new FileInputStream(SERVICE_ACCOUNT_FILE)) {
            googleCredentials = GoogleCredentials.fromStream(serviceAccountStream)
                    .createScoped(Collections.singletonList(SCOPES));
            googleCredentials.refreshIfExpired();
        }

        return googleCredentials.getAccessToken().getTokenValue();
    }
}
