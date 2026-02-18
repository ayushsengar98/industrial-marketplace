package com.marketplace.vendor_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.marketplace.vendor_service.model.Vendor;
import com.marketplace.vendor_service.repository.VendorRepository;

@Service
public class VendorService {

    private final VendorRepository repo;

    public VendorService(VendorRepository repo){
        this.repo=repo;
    }

    public Vendor apply(Vendor v,String email){
        v.setEmail(email);
        return repo.save(v);
    }

    public Vendor status(String email){
        return repo.findByEmail(email).orElseThrow();
    }

    public List<Vendor> pending(){
        return repo.findAll().stream()
                .filter(v->v.getStatus()== Vendor.Status.PENDING)
                .toList();
    }

    public Vendor approve(Long id){
        Vendor v=repo.findById(id).orElseThrow();
        v.setStatus(Vendor.Status.APPROVED);
        return repo.save(v);
    }
}
