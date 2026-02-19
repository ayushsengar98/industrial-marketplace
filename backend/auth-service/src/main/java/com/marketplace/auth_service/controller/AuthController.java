package com.marketplace.auth_service.controller;

import com.marketplace.auth_service.model.User;
import com.marketplace.auth_service.service.AuthService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        return service.login(user.getEmail(), user.getPassword());
    }

    @PostMapping("/refresh")
    public Map<String,String> refresh(@RequestBody Map<String,String> body){
        String refreshToken = body.get("refreshToken");
        System.out.println("Received refresh token: " + refreshToken);
        return service.refresh(refreshToken);
    }

    @PostMapping("/logout")
    public Map<String,String> logout(@RequestBody Map<String,String> body){
        return service.logout(body.get("refreshToken"));
    }

    @PostMapping("/change-password")
    public Map<String,String> changePassword(
            @RequestBody Map<String,String> body,
            @AuthenticationPrincipal String email) {
        return service.changePassword(
            email,
            body.get("oldPassword"),
            body.get("newPassword")
        );
    }

    @PostMapping("/forgot-password")
    public Map<String,String> forgotPassword(@RequestBody Map<String,String> body) {
        return service.forgotPassword(body.get("email"));
    }

    @PostMapping("/reset-password")
    public Map<String,String> resetPassword(@RequestBody Map<String,String> body) {
        return service.resetPassword(
            body.get("token"),
            body.get("newPassword")
        );
    }

    @GetMapping("/profile")
    public Map<String,String> getProfile(@AuthenticationPrincipal String email) {
        return service.getUserProfile(email);
    }

    @PutMapping("/profile")
    public Map<String,String> updateProfile(
            @RequestBody Map<String,String> updates,
            @AuthenticationPrincipal String email) {
        return service.updateUserProfile(email, updates);
    }
}