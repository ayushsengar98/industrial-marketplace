package com.marketplace.order_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Double unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double lineTotal;

    @Column(length = 500)
    private String imageUrl;

    private String vendorEmail;

    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Double getUnitPrice() { return unitPrice; }
    public Integer getQuantity() { return quantity; }
    public Double getLineTotal() { return lineTotal; }
    public String getImageUrl() { return imageUrl; }
    public String getVendorEmail() { return vendorEmail; }

    public void setId(Long id) { this.id = id; }
    public void setOrder(Order order) { this.order = order; }
    public void setProductId(Long productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setLineTotal(Double lineTotal) { this.lineTotal = lineTotal; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setVendorEmail(String vendorEmail) { this.vendorEmail = vendorEmail; }
}
