package com.backend.service;

import com.backend.models.Parents;
import com.backend.repository.ParentRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class ParentService {
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private Cloudinary cloudinary;

    public String addParent(Parents parent, MultipartFile image) throws IOException {
        Parents savedUser = parentRepository.findByPhoneNo(parent.getPhoneNo());
        if(savedUser!=null) {
            return "parent already saved!";
        }
        parent.setId(UUID.randomUUID().toString());
        if(image!=null && !image.isEmpty()) {
            Map options = ObjectUtils.asMap(
                    "folder", "parents-images"
            );
            Map uploadResult = cloudinary.uploader().upload(image.getBytes(), options);
            parent.setPhoto_url((String) uploadResult.get("secure_url"));
        }
        parentRepository.save(parent);
        return "parent added!";
    }
}
