package com.marketplace.vendor_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marketplace.vendor_service.model.Vendor;

public interface VendorRepository extends JpaRepository<Vendor,Long> {
    Optional<Vendor> findByEmail(String email);
}
