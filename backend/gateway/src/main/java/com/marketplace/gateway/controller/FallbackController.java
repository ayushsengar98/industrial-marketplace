package com.marketplace.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public Mono<Map<String, String>> authFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Auth service is temporarily unavailable. Please try again later.");
        response.put("status", "503");
        return Mono.just(response);
    }

    @GetMapping("/product")
    public Mono<Map<String, String>> productFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product service is temporarily unavailable.");
        response.put("status", "503");
        return Mono.just(response);
    }

    @GetMapping("/vendor")
    public Mono<Map<String, String>> vendorFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Vendor service is temporarily unavailable.");
        response.put("status", "503");
        return Mono.just(response);
    }
}