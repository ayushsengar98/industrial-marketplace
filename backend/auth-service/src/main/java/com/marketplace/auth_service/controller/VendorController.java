package com.marketplace.auth_service.controller;

import com.marketplace.auth_service.dto.VendorApplyRequest;
import com.marketplace.auth_service.model.Vendor;
import com.marketplace.auth_service.service.VendorService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor")
public class VendorController {

    private final VendorService service;

    public VendorController(VendorService service){
        this.service=service;
    }

    // USER APPLY
    @PostMapping("/apply")
    public Vendor apply(@RequestBody VendorApplyRequest req,
                        Authentication auth){

        String email = auth.getName();

        return service.apply(
                email,
                req.getCompanyName(),
                req.getGstNumber()
        );
    }

    // ADMIN VIEW PENDING
    @GetMapping("/pending")
    public List<Vendor> pending(){
        return service.pending();
    }

    // ADMIN APPROVE
    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id){
        return service.approve(id);
    }

    // ADMIN REJECT
    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id){
        return service.reject(id);
    }
}
