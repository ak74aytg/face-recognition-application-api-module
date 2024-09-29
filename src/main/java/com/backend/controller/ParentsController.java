package com.backend.controller;

import com.backend.models.Parents;
import com.backend.models.User;
import com.backend.service.ParentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/parents")
public class ParentsController {
    @Autowired
    private ParentService parentService;

    @PostMapping
    public String addParentUser(@RequestParam("parent") String parentJson,
                                @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Parents parent = objectMapper.readValue(parentJson, Parents.class);
        return parentService.addParent(parent, profilePicture);
    }

}
