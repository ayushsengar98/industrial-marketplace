package com.marketplace.order_service.service;

import com.marketplace.order_service.dto.InternalProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${product.service.url:http://localhost:8083}")
    private String productServiceUrl;

    @Value("${internal.api-key:internal-dev-key}")
    private String internalApiKey;

    public InternalProductResponse getProduct(Long productId) {
        String url = productServiceUrl + "/internal/products/" + productId;
        HttpEntity<Void> request = new HttpEntity<>(internalHeaders());
        ResponseEntity<InternalProductResponse> response =
            restTemplate.exchange(url, HttpMethod.GET, request, InternalProductResponse.class);
        return response.getBody();
    }

    public InternalProductResponse reserveStock(Long productId, int quantity) {
        String url = productServiceUrl + "/internal/products/" + productId + "/reserve";
        HttpEntity<String> request = new HttpEntity<>("{\"quantity\":" + quantity + "}", internalHeadersWithJson());
        ResponseEntity<InternalProductResponse> response =
            restTemplate.exchange(url, HttpMethod.POST, request, InternalProductResponse.class);
        return response.getBody();
    }

    private HttpHeaders internalHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Internal-Api-Key", internalApiKey);
        return headers;
    }

    private HttpHeaders internalHeadersWithJson() {
        HttpHeaders headers = internalHeaders();
        headers.set("Content-Type", "application/json");
        return headers;
    }
}
