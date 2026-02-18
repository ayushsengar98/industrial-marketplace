package com.marketplace.vendor_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String companyName;
    private String gstNumber;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    public Long getId(){ return id; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email=email; }

    public String getCompanyName(){ return companyName; }
    public void setCompanyName(String companyName){ this.companyName=companyName; }

    public String getGstNumber(){ return gstNumber; }
    public void setGstNumber(String gstNumber){ this.gstNumber=gstNumber; }

    public Status getStatus(){ return status; }
    public void setStatus(Status status){ this.status=status; }
}
