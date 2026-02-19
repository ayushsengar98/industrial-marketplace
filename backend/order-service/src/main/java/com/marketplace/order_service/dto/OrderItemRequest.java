package com.marketplace.order_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderItemRequest {
    @NotNull(message = "Product id is required")
    private Long productId;

    private String productName;

    @Min(value = 0, message = "Price must be non-negative")
    private Double unitPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String imageUrl;

    private String vendorEmail;

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Double getUnitPrice() { return unitPrice; }
    public Integer getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }
    public String getVendorEmail() { return vendorEmail; }

    public void setProductId(Long productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setVendorEmail(String vendorEmail) { this.vendorEmail = vendorEmail; }
}
