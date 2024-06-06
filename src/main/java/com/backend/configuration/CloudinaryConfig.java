package com.backend.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudConfig() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dzqcuwrq3",
                "api_key", "953456548585289",
                "api_secret", "MOq2qY6JKeSLWXs_6W6YS_oQD-A",
                "secure", true));
    }
}
