package com.marketplace.order_service.dto;

import com.marketplace.order_service.model.Order;
import com.marketplace.order_service.model.OrderItem;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponse {
    private Long id;
    private String buyerEmail;
    private Double totalAmount;
    private String status;
    private Instant createdAt;
    private List<OrderItemResponse> items;

    public Long getId() { return id; }
    public String getBuyerEmail() { return buyerEmail; }
    public Double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public List<OrderItemResponse> getItems() { return items; }

    public void setId(Long id) { this.id = id; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }

    public static OrderResponse fromEntity(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setBuyerEmail(order.getBuyerEmail());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus().name());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(order.getItems().stream().map(OrderResponse::mapItem).collect(Collectors.toList()));
        return response;
    }

    private static OrderItemResponse mapItem(OrderItem item) {
        OrderItemResponse dto = new OrderItemResponse();
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setQuantity(item.getQuantity());
        dto.setLineTotal(item.getLineTotal());
        dto.setImageUrl(item.getImageUrl());
        dto.setVendorEmail(item.getVendorEmail());
        return dto;
    }
}
