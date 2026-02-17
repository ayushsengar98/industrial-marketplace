package com.marketplace.auth_service.model;

import jakarta.persistence.*;

@Entity
@Table(name="vendors")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String companyName;

    private String gstNumber;

    private String status; // PENDING / APPROVED / REJECTED

    public Long getId(){ return id; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email=email; }

    public String getCompanyName(){ return companyName; }
    public void setCompanyName(String companyName){ this.companyName=companyName; }

    public String getGstNumber(){ return gstNumber; }
    public void setGstNumber(String gstNumber){ this.gstNumber=gstNumber; }

    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status=status; }
}
