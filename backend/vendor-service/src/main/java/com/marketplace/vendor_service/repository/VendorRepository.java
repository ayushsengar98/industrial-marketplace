package com.marketplace.vendor_service.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.marketplace.vendor_service.model.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    
    // Find by email (for checking existing applications)
    Optional<Vendor> findByEmail(String email);
    
    // Find all vendors with specific status (PENDING, APPROVED, REJECTED)
    List<Vendor> findByStatus(Vendor.Status status);
    
    // Optional: Count by status (useful for dashboard)
    long countByStatus(Vendor.Status status);
}