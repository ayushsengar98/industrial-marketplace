package com.marketplace.auth_service.service;

import com.marketplace.auth_service.model.RefreshToken;
import com.marketplace.auth_service.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    public RefreshTokenService(RefreshTokenRepository repo){
        this.repo=repo;
    }

    private static final long EXPIRATION = 1000*60*60*24;

    public RefreshToken createToken(String email){

        RefreshToken token=new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setEmail(email);
        token.setExpiryDate(Instant.now().plusMillis(EXPIRATION));

        return repo.save(token);
    }

    public String validate(String token){

        RefreshToken rt = repo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if(rt.getExpiryDate().isBefore(Instant.now())){
            repo.delete(rt);
            throw new RuntimeException("Expired refresh token");
        }

        return rt.getEmail();
    }

    @Transactional
    public void delete(String token){
        repo.deleteByToken(token);
    }
}
