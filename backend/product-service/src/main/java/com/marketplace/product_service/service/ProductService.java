package com.marketplace.product_service.service;

import com.marketplace.product_service.model.Product;
import com.marketplace.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo){
        this.repo=repo;
    }

    public Product add(Product p,String email){
        p.setVendorEmail(email);
        return repo.save(p);
    }

    public List<Product> all(){
        return repo.findAll();
    }

    public List<Product> mine(String email){
        return repo.findByVendorEmail(email);
    }

    public void delete(Long id,String email){
        Product p = repo.findById(id).orElseThrow();
        if(!p.getVendorEmail().equals(email))
            throw new RuntimeException("Not your product");
        repo.delete(p);
    }
}
