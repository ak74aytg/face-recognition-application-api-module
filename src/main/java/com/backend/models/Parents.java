package com.backend.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Parents {
    @Id
    private String id;
    private String name;
    private String photo_url;
    private Long phoneNo;
    private String location;
    private Integer pincode;
    private String parent_name;
}
