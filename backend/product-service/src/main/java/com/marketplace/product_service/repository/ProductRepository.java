package com.marketplace.product_service.repository;

import com.marketplace.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findByVendorEmail(String email);
}
