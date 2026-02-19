package com.marketplace.product_service.controller;

import com.marketplace.product_service.DTO.InternalProductResponse;
import com.marketplace.product_service.DTO.StockReserveRequest;
import com.marketplace.product_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/products")
public class InternalProductController {
    private final ProductService productService;

    @Value("${internal.api-key:internal-dev-key}")
    private String internalApiKey;

    public InternalProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public InternalProductResponse getProduct(
        @PathVariable Long id,
        @RequestHeader(value = "X-Internal-Api-Key", required = false) String providedApiKey
    ) {
        validateInternalApiKey(providedApiKey);
        return productService.getInternalProduct(id);
    }

    @PostMapping("/{id}/reserve")
    public InternalProductResponse reserveStock(
        @PathVariable Long id,
        @Valid @RequestBody StockReserveRequest request,
        @RequestHeader(value = "X-Internal-Api-Key", required = false) String providedApiKey
    ) {
        validateInternalApiKey(providedApiKey);
        return productService.reserveStock(id, request.getQuantity());
    }

    private void validateInternalApiKey(String providedApiKey) {
        if (providedApiKey == null || !providedApiKey.equals(internalApiKey)) {
            throw new RuntimeException("Unauthorized internal request");
        }
    }
}
