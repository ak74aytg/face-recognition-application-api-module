package com.backend.controller;

import com.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    @GetMapping("/")
    public String home(Principal principal) throws Exception {
        return adminService.Home(principal);
    }

    @PostMapping("/delete/{userId}")
    public String deleteUser(Principal principal, @PathVariable String userId) throws Exception{
        return adminService.DeleteUsers(principal, userId);
    }
}
