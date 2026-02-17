package com.marketplace.auth_service.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RoleTestController {

    @GetMapping("/user")
    public String userAccess(){
        return "Hello USER 👤";
    }

    @GetMapping("/admin")
    public String adminAccess(){
        return "Hello ADMIN 👑";
    }
}
