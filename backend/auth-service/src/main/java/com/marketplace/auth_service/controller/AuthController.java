package com.marketplace.auth_service.controller;

import com.marketplace.auth_service.model.User;
import com.marketplace.auth_service.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service){
        this.service=service;
    }

    @PostMapping("/register")
    public Map<String,String> register(@RequestBody User user){
        return service.register(user);
    }

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody User user){
        return service.login(user.getEmail(),user.getPassword());
    }

    @PostMapping("/refresh")
    public Map<String,String> refresh(@RequestBody Map<String,String> body){
        return service.refresh(body.get("refreshToken"));
    }

    @PostMapping("/logout")
    public Map<String,String> logout(@RequestBody Map<String,String> body){
        service.logout(body.get("refreshToken"));
        return Map.of("message","Logged out");
    }
}
