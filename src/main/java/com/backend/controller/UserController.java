package com.backend.controller;

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
    public String home(){
        return "hello user";
    }

    @PostMapping("/location/edit")
    public String edit(Principal principal, @RequestParam("location") String location){
        return userService.editLocation(principal, location);
    }
}
