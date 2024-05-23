package com.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MainController {

    @GetMapping("/user")
    public String home(){
        return "hello user";
    }
    
    @GetMapping("/admin")
    public String mansion() {
    	return "hello admin";
    }
}
