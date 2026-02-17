package com.marketplace.product_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JwtService {

    private static final String SECRET =
            "supersecretkeysupersecretkeysupersecretkey";

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
}
