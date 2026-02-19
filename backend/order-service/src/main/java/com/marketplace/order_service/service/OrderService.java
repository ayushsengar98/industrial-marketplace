package com.marketplace.order_service.service;

import com.marketplace.order_service.dto.OrderItemRequest;
import com.marketplace.order_service.dto.OrderRequest;
import com.marketplace.order_service.dto.OrderResponse;
import com.marketplace.order_service.dto.InternalProductResponse;
import com.marketplace.order_service.model.Order;
import com.marketplace.order_service.model.OrderItem;
import com.marketplace.order_service.model.OrderStatus;
import com.marketplace.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    @Transactional
    public OrderResponse placeOrder(OrderRequest request, String buyerEmail) {
        Order order = new Order();
        order.setBuyerEmail(buyerEmail);
        order.setStatus(OrderStatus.PLACED);

        double total = 0.0;
        for (OrderItemRequest reqItem : request.getItems()) {
            InternalProductResponse product = productClient.getProduct(reqItem.getProductId());
            if (product == null) {
                throw new RuntimeException("Product not found: " + reqItem.getProductId());
            }
            productClient.reserveStock(reqItem.getProductId(), reqItem.getQuantity());

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(reqItem.getProductId());
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(reqItem.getQuantity());
            item.setLineTotal(product.getPrice() * reqItem.getQuantity());
            item.setImageUrl(product.getImageUrl());
            item.setVendorEmail(product.getVendorEmail());
            order.getItems().add(item);
            total += item.getLineTotal();
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        return OrderResponse.fromEntity(saved);
    }

    public List<OrderResponse> getMyOrders(String buyerEmail) {
        return orderRepository.findByBuyerEmailOrderByCreatedAtDesc(buyerEmail)
            .stream()
            .map(OrderResponse::fromEntity)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getVendorOrders(String vendorEmail) {
        return orderRepository.findVendorOrders(vendorEmail)
            .stream()
            .map(OrderResponse::fromEntity)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(OrderResponse::fromEntity)
            .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String statusValue, String requesterEmail, boolean isAdmin, boolean isVendor) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (!isAdmin && !isVendor) {
            throw new RuntimeException("Only vendor or admin can update order status");
        }
        if (isVendor && !orderContainsVendor(order, requesterEmail)) {
            throw new RuntimeException("You can update only your own vendor orders");
        }

        OrderStatus next = parseStatus(statusValue);
        validateTransition(order.getStatus(), next);
        order.setStatus(next);
        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    private boolean orderContainsVendor(Order order, String vendorEmail) {
        return order.getItems().stream().anyMatch(item -> vendorEmail.equalsIgnoreCase(item.getVendorEmail()));
    }

    private OrderStatus parseStatus(String value) {
        try {
            return OrderStatus.valueOf(value.trim().toUpperCase());
        } catch (Exception ex) {
            throw new RuntimeException("Invalid order status: " + value);
        }
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        if (current == next) {
            return;
        }
        switch (current) {
            case PLACED -> {
                if (!Set.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED).contains(next)) {
                    throw new RuntimeException("Invalid status transition");
                }
            }
            case PROCESSING -> {
                if (!Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED).contains(next)) {
                    throw new RuntimeException("Invalid status transition");
                }
            }
            case SHIPPED -> {
                if (!Set.of(OrderStatus.DELIVERED).contains(next)) {
                    throw new RuntimeException("Invalid status transition");
                }
            }
            case DELIVERED, CANCELLED -> throw new RuntimeException("Finalized orders cannot change status");
        }
    }
}
