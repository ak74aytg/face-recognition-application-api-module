package com.backend.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageData {
    @Id
    private String id;
    private String name;
    private String imageUrl;
}