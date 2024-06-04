package com.backend.service;

import com.backend.models.ImageData;
import com.backend.models.User;
import com.backend.repository.ImageDataRepository;
import com.backend.repository.UserRepository;
import com.backend.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageServices {
    @Value("${project.image}")
    private String path;
    @Autowired
    ImageDataRepository imageRepository;
    @Autowired
    UserRepository userRepository;

    public String SaveImages(Principal principal, ImageData imageData, MultipartFile image) throws IOException {
        User user = userRepository.findByEmail(principal.getName());
        if(user==null){
            throw new BadCredentialsException("you are not allowed to access this api!");
        }
        imageData.setId(UUID.randomUUID().toString());
        String fileName = imageData.getId()+"_"+imageData.getName()+"_"+image.getOriginalFilename();
        String filePath = path+ File.separator+fileName;
        File f = new File(path);
        if(!f.exists()) {
            f.mkdir();
        }
        Files.copy(image.getInputStream(), Paths.get(filePath));
        imageData.setImageUrl(fileName);
        user.getSavedImages().add(imageData);
        userRepository.save(user);
        imageRepository.save(imageData);
        return "saved successfully";
    }

    public List<ImageData> getImages(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        if(user==null){
            throw new BadCredentialsException("you are not allowed to access this api!");
        }
        return user.getSavedImages();
    }

    public String deleteImage(String imageId, Principal principal) throws IOException {
        User user = userRepository.findByEmail(principal.getName());
        if(user==null){
            throw new BadCredentialsException("you are not allowed to access this api!");
        }
        Optional<ImageData> OptionalImageData = imageRepository.findById(imageId);
        if(OptionalImageData.isEmpty()){
            throw new BadCredentialsException("imageData does not exist!");
        }
        ImageData imageData = OptionalImageData.get();

        boolean flag = false;
        for(ImageData img : user.getSavedImages()){
            if(img.getId().equals(imageId)){
                user.getSavedImages().remove(img);
                flag=true;
            }
        }
        if(!flag){
            throw new RuntimeException("You do not have image data with given id!");
        }

        String FILE_NAME = imageData.getImageUrl();
        Path loc = Paths.get(path+File.separator+FILE_NAME);
        Files.delete(loc);
        System.out.println("photo is deleted");
        imageRepository.delete(imageData);
        return "image deleted succesfully";
    }
}
