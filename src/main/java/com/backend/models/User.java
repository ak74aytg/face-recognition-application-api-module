package com.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class User {
    @Id
    String id;
    String name;
    String email;
    String password;
    String location;
    Integer pincode;
    String profile_url;
    String token;
    @DBRef
    @JsonIgnore
    private List<ImageData> savedImages = new ArrayList<>();
    @JsonIgnore
    private List<Map<String, String>> userNotifications = new ArrayList<>();
}