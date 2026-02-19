package com.marketplace.order_service.controller;

import com.marketplace.order_service.dto.OrderRequest;
import com.marketplace.order_service.dto.OrderResponse;
import com.marketplace.order_service.dto.OrderStatusUpdateRequest;
import com.marketplace.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse placeOrder(@Valid @RequestBody OrderRequest request, Principal principal) {
        return orderService.placeOrder(request, principal.getName());
    }

    @GetMapping("/my")
    public List<OrderResponse> myOrders(Principal principal) {
        return orderService.getMyOrders(principal.getName());
    }

    @GetMapping("/vendor")
    public List<OrderResponse> vendorOrders(Authentication authentication) {
        if (!hasRole(authentication, "ROLE_VENDOR")) {
            throw new RuntimeException("Only vendors can access this endpoint");
        }
        return orderService.getVendorOrders(authentication.getName());
    }

    @GetMapping("/all")
    public List<OrderResponse> allOrders(Authentication authentication) {
        if (!hasRole(authentication, "ROLE_ADMIN")) {
            throw new RuntimeException("Only admins can access this endpoint");
        }
        return orderService.getAllOrders();
    }

    @PutMapping("/{id}/status")
    public OrderResponse updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody OrderStatusUpdateRequest request,
        Authentication authentication
    ) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        boolean isVendor = hasRole(authentication, "ROLE_VENDOR");
        return orderService.updateOrderStatus(id, request.getStatus(), authentication.getName(), isAdmin, isVendor);
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> role.equals(auth.getAuthority()));
    }
}
