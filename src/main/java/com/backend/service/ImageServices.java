package com.backend.service;



import com.backend.models.ImageData;
import com.backend.models.User;
import com.backend.repository.ImageDataRepository;
import com.backend.repository.UserRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Service
public class ImageServices {
    @Autowired
    Cloudinary cloudinary; // Inject Cloudinary bean

    @Autowired
    ImageDataRepository imageRepository;

    @Autowired
    UserRepository userRepository;

    public String saveImage(Principal principal, ImageData imageData, MultipartFile image) throws IOException {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            throw new BadCredentialsException("You are not allowed to access this API!");
        }

        imageData.setId(UUID.randomUUID().toString());

        // Upload image to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());

        // Save image URL and other data
        imageData.setImageUrl((String) uploadResult.get("secure_url")); // Use secure URL for HTTPS
        user.getSavedImages().add(imageData);
        userRepository.save(user);
        imageRepository.save(imageData);
        return "Saved successfully";
    }

    public List<ImageData> getImages(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            throw new BadCredentialsException("You are not allowed to access this API!");
        }
        return user.getSavedImages();
    }

    public String deleteImage(String imageId, Principal principal) throws IOException {
        // Fetch image data from repository
        Optional<ImageData> OptionalImageData = imageRepository.findById(imageId);
        if(OptionalImageData.isEmpty()){
            throw new BadCredentialsException("imageData does not exist!");
        }
        ImageData imageData = OptionalImageData.get();
        String url = imageData.getImageUrl();
        int lastDotIndex = url.lastIndexOf('.');
        int lastSlashIndex = url.lastIndexOf('/');
        String imageName = url.substring(lastSlashIndex + 1, lastDotIndex);

        try {
            ApiResponse apiResponse = cloudinary.api().deleteResources(Collections.singletonList(imageName),
                    ObjectUtils.asMap("type", "upload", "resource_type", "image"));
            System.out.println(apiResponse);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        // Delete the image data from the repository
        imageRepository.delete(imageData);

        return "image deleted successfully";
    }
}
