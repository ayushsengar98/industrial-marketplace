package com.marketplace.order_service.dto;

public class OrderItemResponse {
    private Long productId;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
    private Double lineTotal;
    private String imageUrl;
    private String vendorEmail;

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Double getUnitPrice() { return unitPrice; }
    public Integer getQuantity() { return quantity; }
    public Double getLineTotal() { return lineTotal; }
    public String getImageUrl() { return imageUrl; }
    public String getVendorEmail() { return vendorEmail; }

    public void setProductId(Long productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setLineTotal(Double lineTotal) { this.lineTotal = lineTotal; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setVendorEmail(String vendorEmail) { this.vendorEmail = vendorEmail; }
}
