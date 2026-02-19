package com.marketplace.product_service.DTO;

public class InternalProductResponse {
    private Long productId;
    private String name;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private String vendorEmail;

    public Long getProductId() { return productId; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public Integer getStock() { return stock; }
    public String getImageUrl() { return imageUrl; }
    public String getVendorEmail() { return vendorEmail; }

    public void setProductId(Long productId) { this.productId = productId; }
    public void setName(String name) { this.name = name; }
    public void setPrice(Double price) { this.price = price; }
    public void setStock(Integer stock) { this.stock = stock; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setVendorEmail(String vendorEmail) { this.vendorEmail = vendorEmail; }
}
