package com.marketplace.vendor_service.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marketplace.vendor_service.model.Vendor;
import com.marketplace.vendor_service.service.VendorService;

@RestController
@RequestMapping("/api/vendor")
public class VendorController {

    private final VendorService service;

    public VendorController(VendorService service){
        this.service=service;
    }

    @PostMapping("/apply")
    public Vendor apply(@RequestBody Vendor v, Principal p){
        return service.apply(v,p.getName());
    }

    @GetMapping("/status")
    public Vendor status(Principal p){
        return service.status(p.getName());
    }

    @GetMapping("/pending")
    public List<Vendor> pending(){
        return service.pending();
    }

    @PostMapping("/approve/{id}")
    public Vendor approve(@PathVariable Long id){
        return service.approve(id);
    }
}
