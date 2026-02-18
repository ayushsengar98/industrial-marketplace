package com.marketplace.vendor_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "supersecretkeysupersecretkeysupersecretkey";
    
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    private Key key(){
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public Claims parse(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public boolean validateToken(String token) {
        try {
            Claims claims = parse(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}