package com.tiezshop.service;

import com.tiezshop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    
    Order createOrder(Order order);
    
    Order updateOrder(String orderId, Order order);
    
    void cancelOrder(String orderId);
    
    Order getOrderById(String orderId);
    
    Order getOrderByOrderNumber(String orderNumber);
    
    List<Order> getOrdersByUserId(String userId);
    
    Page<Order> getOrdersByUserId(String userId, Pageable pageable);
    
    List<Order> getOrdersByStatus(Order.OrderStatus status);
    
    List<Order> getOrdersByPaymentStatus(Order.PaymentStatus paymentStatus);
    
    List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    Order updateOrderStatus(String orderId, Order.OrderStatus status);
    
    Order updatePaymentStatus(String orderId, Order.PaymentStatus paymentStatus);
    
    Long getOrderCountByStatus(Order.OrderStatus status);
    
    Double getTotalRevenueByStatusAndDateRange(Order.OrderStatus status, 
                                            LocalDateTime startDate, 
                                            LocalDateTime endDate);
    
    List<Order> getRecentOrdersByUserId(String userId, int limit);
}
