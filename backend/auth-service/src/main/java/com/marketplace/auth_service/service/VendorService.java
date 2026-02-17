package com.marketplace.auth_service.service;

import com.marketplace.auth_service.model.*;
import com.marketplace.auth_service.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorService {

    private final VendorRepository vendorRepo;
    private final UserRepository userRepo;

    public VendorService(VendorRepository vendorRepo,
                         UserRepository userRepo){
        this.vendorRepo = vendorRepo;
        this.userRepo = userRepo;
    }

    // USER APPLY
    public Vendor apply(String email,String company,String gst){

        Vendor v = new Vendor();
        v.setEmail(email);
        v.setCompanyName(company);
        v.setGstNumber(gst);
        v.setStatus("PENDING");

        return vendorRepo.save(v);
    }

    // ADMIN VIEW PENDING
    public List<Vendor> pending(){
        return vendorRepo.findByStatus("PENDING");
    }

    // ADMIN APPROVE
    public String approve(Long id){

        Vendor v = vendorRepo.findById(id)
                .orElseThrow();

        v.setStatus("APPROVED");
        vendorRepo.save(v);

        // promote user → VENDOR
        User u = userRepo.findByEmail(v.getEmail()).orElseThrow();
        u.setRole(Role.VENDOR);
        userRepo.save(u);

        return "Vendor Approved";
    }

    // ADMIN REJECT
    public String reject(Long id){
        Vendor v = vendorRepo.findById(id).orElseThrow();
        v.setStatus("REJECTED");
        vendorRepo.save(v);
        return "Vendor Rejected";
    }
}
