package com.marketplace.auth_service.service;

import com.marketplace.auth_service.model.Role;
import com.marketplace.auth_service.model.User;
import com.marketplace.auth_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository repo,
                       PasswordEncoder encoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService){
        this.repo=repo;
        this.encoder=encoder;
        this.jwtService=jwtService;
        this.refreshTokenService=refreshTokenService;
    }

    // REGISTER
    public Map<String,String> register(User user){

        if(repo.findByEmail(user.getEmail()).isPresent())
            throw new RuntimeException("Email already exists");

        user.setPassword(encoder.encode(user.getPassword()));

        // 🔒 Always USER for public signup
        user.setRole(Role.USER);

        repo.save(user);

        return Map.of("message","Registered successfully");
    }

    // LOGIN
    public Map<String,String> login(String email,String password){

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if(!encoder.matches(password,user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        String access = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        String refresh = refreshTokenService.createToken(email).getToken();

        return Map.of(
                "accessToken",access,
                "refreshToken",refresh
        );
    }

    // REFRESH
    public Map<String,String> refresh(String refreshToken){

        String email = refreshTokenService.validate(refreshToken);

        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String access = jwtService.generateToken(
                email,
                user.getRole().name()
        );

        return Map.of("accessToken",access);
    }

    // LOGOUT
    public void logout(String refreshToken){
        refreshTokenService.delete(refreshToken);
    }
}
