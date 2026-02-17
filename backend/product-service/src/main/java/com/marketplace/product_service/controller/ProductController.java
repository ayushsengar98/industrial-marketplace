package com.marketplace.product_service.controller;

import com.marketplace.product_service.model.Product;
import com.marketplace.product_service.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service){
        this.service=service;
    }

    @PostMapping
    public Product add(@RequestBody Product p, Principal principal){
        return service.add(p,principal.getName());
    }

    @GetMapping
    public List<Product> all(){
        return service.all();
    }

    @GetMapping("/mine")
    public List<Product> mine(Principal principal){
        return service.mine(principal.getName());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,Principal principal){
        service.delete(id,principal.getName());
    }
}
