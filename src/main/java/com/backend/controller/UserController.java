package com.backend.controller;

import com.backend.models.User;
import com.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

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
    public String edit(Principal principal,@RequestParam(value = "pincode", required = false) Integer pincode, @RequestParam(value = "location", required = false) String location){
        return userService.editLocation(principal, location, pincode);
    }

    @PostMapping("/token/edit")
    public String editToken(@RequestParam("token") String token, Principal principal){
        return userService.editToken(token, principal);
    }

    @GetMapping("/notifications")
    public List<Map<String, String>> getNotifications(Principal principal){
        return userService.getNotifications(principal);
    }

    @PostMapping("notifications/delete/all")
    public String deleteAllNotifications(Principal principal){
        return userService.deleteAllNotifications(principal);
    }
    @PostMapping("notifications/delete")
    public String deleteNotification(Principal principal, @RequestBody Map<String, String> notification){
        return userService.deleteNotification(principal, notification.get("n_id"));
    }
}
