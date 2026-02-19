package com.marketplace.auth_service.service;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marketplace.auth_service.model.User;
import com.marketplace.auth_service.model.Role;
import com.marketplace.auth_service.repository.UserRepository;
import com.marketplace.auth_service.repository.RefreshTokenRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    // Password validation pattern (min 8 chars, 1 letter, 1 number)
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");

    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService,
                      RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Register a new user
     */
    @Transactional
    public Map<String, String> register(User user) {
        // Validate email
        validateEmail(user.getEmail());
        
        // Validate password
        validatePassword(user.getPassword());
        
        // Check if user already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Encode password and set role
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        
        // Save user
        User savedUser = userRepository.save(user);

        // Return success response
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("userId", savedUser.getId().toString());
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole().name());
        
        return response;
    }

    /**
     * Login user and generate tokens
     */
    public Map<String, String> login(String email, String password) {
        // Find user
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate tokens
        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // Return response
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("role", user.getRole().name());
        response.put("email", user.getEmail());
        response.put("userId", user.getId().toString());
        
        return response;
    }

    /**
     * Refresh access token using refresh token
     */
    public Map<String, String> refresh(String refreshToken) {
        try {
            // Validate refresh token
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new RuntimeException("Refresh token is required");
            }
            
            if (!jwtService.validateToken(refreshToken)) {
                throw new RuntimeException("Invalid or expired refresh token");
            }
            
            // Extract email from token
            String email = jwtService.extractEmail(refreshToken);
            
            // Find user
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate new access token
            String newAccessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
            
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());
            
            return response;
            
        } catch (Exception e) {
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }

    /**
     * Logout user - invalidate refresh token
     */
    @Transactional
    public Map<String, String> logout(String refreshToken) {
        try {
            // If using database tokens, delete them
            // refreshTokenRepository.deleteByToken(refreshToken);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged out successfully");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }

    /**
     * Change user password
     */
    @Transactional
    public Map<String, String> changePassword(String email, String oldPassword, String newPassword) {
        // Find user
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Validate new password
        validatePassword(newPassword);

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        
        return response;
    }

    /**
     * Forgot password - send reset email
     */
    public Map<String, String> forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email not found"));

        // Generate password reset token
        String resetToken = jwtService.generatePasswordResetToken(email);
        
        // In production, send email with reset link
        System.out.println("Password reset link: http://localhost:3000/reset-password?token=" + resetToken);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset email sent");
        // Remove resetToken in production
        response.put("resetToken", resetToken); 
        
        return response;
    }

    /**
     * Reset password with token
     */
    @Transactional
    public Map<String, String> resetPassword(String token, String newPassword) {
        try {
            // Validate token
            if (!jwtService.validatePasswordResetToken(token)) {
                throw new RuntimeException("Invalid or expired reset token");
            }

            // Extract email
            String email = jwtService.extractEmail(token);
            
            // Find user
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate and update password
            validatePassword(newPassword);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successfully");
            
            return response;
            
        } catch (Exception e) {
            throw new RuntimeException("Password reset failed: " + e.getMessage());
        }
    }

    /**
     * Get user profile
     */
    public Map<String, String> getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, String> profile = new HashMap<>();
        profile.put("userId", user.getId().toString());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole().name());
        
        return profile;
    }

    /**
     * Update user profile
     */
    @Transactional
    public Map<String, String> updateUserProfile(String email, Map<String, String> updates) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields if provided
        if (updates.containsKey("email")) {
            String newEmail = updates.get("email");
            validateEmail(newEmail);
            
            // Check if email already exists
            if (!newEmail.equals(email) && userRepository.findByEmail(newEmail).isPresent()) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(newEmail);
        }

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Profile updated successfully");
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        
        return response;
    }

    @Transactional
    public Map<String, String> updateUserRole(String email, Role role) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(role);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User role updated successfully");
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        return response;
    }

    public List<Map<String, String>> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
            .map(user -> {
                Map<String, String> item = new HashMap<>();
                item.put("userId", user.getId().toString());
                item.put("email", user.getEmail());
                item.put("role", user.getRole().name());
                return item;
            })
            .collect(Collectors.toList());
    }

    /**
     * Validate email format
     */
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new RuntimeException("Invalid email format");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        if (password.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new RuntimeException("Password must contain at least one letter and one number");
        }
    }
}
