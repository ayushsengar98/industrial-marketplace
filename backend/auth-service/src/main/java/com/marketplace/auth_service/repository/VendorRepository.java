package com.marketplace.auth_service.repository;

import com.marketplace.auth_service.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorRepository extends JpaRepository<Vendor,Long> {

    List<Vendor> findByStatus(String status);
}
