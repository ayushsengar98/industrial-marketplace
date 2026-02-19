package com.marketplace.order_service.repository;

import com.marketplace.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerEmailOrderByCreatedAtDesc(String buyerEmail);
    List<Order> findAllByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.vendorEmail = :vendorEmail ORDER BY o.createdAt DESC")
    List<Order> findVendorOrders(@Param("vendorEmail") String vendorEmail);
}
