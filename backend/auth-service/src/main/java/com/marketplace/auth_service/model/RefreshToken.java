package com.marketplace.auth_service.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private String token;

    private String email;
    private Instant expiryDate;

    public Long getId(){ return id; }

    public String getToken(){ return token; }
    public void setToken(String token){ this.token=token; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email=email; }

    public Instant getExpiryDate(){ return expiryDate; }
    public void setExpiryDate(Instant expiryDate){ this.expiryDate=expiryDate; }
}
