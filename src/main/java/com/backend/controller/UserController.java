package com.backend.controller;

import com.backend.models.User;
import com.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public User home(Principal principal){
        return userService.getCurrentUser(principal);
    }

    @PostMapping("/location/edit")
    public String edit(Principal principal, @RequestParam("location") String location){
        return userService.editLocation(principal, location);
    }
}
