package com.marketplace.product_service.service;
import com.marketplace.product_service.DTO.ProductRequest;
import com.marketplace.product_service.DTO.ProductResponse;
import com.marketplace.product_service.DTO.InternalProductResponse;
import com.marketplace.product_service.model.Product;
import com.marketplace.product_service.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse addProduct(@Valid ProductRequest request, String vendorEmail) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setVendorEmail(vendorEmail);
        
        Product savedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(savedProduct);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<ProductResponse> getAllProductsPaginated(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return productRepository.findAll(pageable)
                .map(ProductResponse::fromEntity);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return ProductResponse.fromEntity(product);
    }

    public InternalProductResponse getInternalProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return toInternalResponse(product);
    }

    public List<ProductResponse> getVendorProducts(String vendorEmail) {
        return productRepository.findByVendorEmail(vendorEmail)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<ProductResponse> getVendorProductsPaginated(String vendorEmail, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByVendorEmail(vendorEmail, pageable)
                .map(ProductResponse::fromEntity);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, @Valid ProductRequest request, String vendorEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (!product.getVendorEmail().equals(vendorEmail)) {
            throw new RuntimeException("You don't have permission to update this product");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());

        Product updatedProduct = productRepository.save(product);
        return ProductResponse.fromEntity(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id, String vendorEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (!product.getVendorEmail().equals(vendorEmail)) {
            throw new RuntimeException("You don't have permission to delete this product");
        }

        productRepository.delete(product);
    }

    public List<ProductResponse> searchProducts(String category, Double minPrice, Double maxPrice) {
        return productRepository.searchProducts(category, minPrice, maxPrice)
                .stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public boolean isProductOwner(Long id, String vendorEmail) {
        return productRepository.existsByIdAndVendorEmail(id, vendorEmail);
    }

    @Transactional
    public InternalProductResponse reserveStock(Long id, int quantity) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (quantity < 1) {
            throw new RuntimeException("Quantity must be at least 1");
        }
        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        product.setStock(product.getStock() - quantity);
        Product saved = productRepository.save(product);
        return toInternalResponse(saved);
    }

    private InternalProductResponse toInternalResponse(Product product) {
        InternalProductResponse response = new InternalProductResponse();
        response.setProductId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setImageUrl(product.getImageUrl());
        response.setVendorEmail(product.getVendorEmail());
        return response;
    }
}
