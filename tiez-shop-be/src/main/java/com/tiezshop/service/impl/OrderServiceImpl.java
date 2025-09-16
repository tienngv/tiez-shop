package com.tiezshop.service.impl;

import com.tiezshop.entity.Order;
import com.tiezshop.repository.OrderRepository;
import com.tiezshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Order createOrder(Order order) {
        log.info("Creating order: {}", order.getOrderNumber());
        
        // Generate order number if not provided
        if (order.getOrderNumber() == null || order.getOrderNumber().isEmpty()) {
            order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(String orderId, Order order) {
        log.info("Updating order: {}", orderId);
        
        Order existingOrder = getOrderById(orderId);
        
        // Update fields
        existingOrder.setTotalAmount(order.getTotalAmount());
        existingOrder.setDiscountAmount(order.getDiscountAmount());
        existingOrder.setShippingFee(order.getShippingFee());
        existingOrder.setTaxAmount(order.getTaxAmount());
        existingOrder.setStatus(order.getStatus());
        existingOrder.setPaymentStatus(order.getPaymentStatus());
        existingOrder.setNotes(order.getNotes());
        
        // Update shipping information
        existingOrder.setShippingName(order.getShippingName());
        existingOrder.setShippingPhone(order.getShippingPhone());
        existingOrder.setShippingAddress(order.getShippingAddress());
        existingOrder.setShippingCity(order.getShippingCity());
        existingOrder.setShippingDistrict(order.getShippingDistrict());
        existingOrder.setShippingPostalCode(order.getShippingPostalCode());
        
        return orderRepository.save(existingOrder);
    }

    @Override
    public void cancelOrder(String orderId) {
        log.info("Cancelling order: {}", orderId);
        
        Order order = getOrderById(orderId);
        
        if (!order.canBeCancelled()) {
            throw new RuntimeException("Order cannot be cancelled in current status: " + order.getStatus());
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(String orderId) {
        log.info("Getting order by ID: {}", orderId);
        
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderByOrderNumber(String orderNumber) {
        log.info("Getting order by order number: {}", orderNumber);
        
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with order number: " + orderNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUserId(String userId) {
        log.info("Getting orders by user ID: {}", userId);
        
        return orderRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> getOrdersByUserId(String userId, Pageable pageable) {
        log.info("Getting orders by user ID with pagination: {}", userId);
        
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        log.info("Getting orders by status: {}", status);
        
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByPaymentStatus(Order.PaymentStatus paymentStatus) {
        log.info("Getting orders by payment status: {}", paymentStatus);
        
        return orderRepository.findByPaymentStatus(paymentStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting orders by date range: {} to {}", startDate, endDate);
        
        return orderRepository.findByCreatedTimeBetween(startDate, endDate);
    }

    @Override
    public Order updateOrderStatus(String orderId, Order.OrderStatus status) {
        log.info("Updating order status: {} to {}", orderId, status);
        
        Order order = getOrderById(orderId);
        order.setStatus(status);
        
        return orderRepository.save(order);
    }

    @Override
    public Order updatePaymentStatus(String orderId, Order.PaymentStatus paymentStatus) {
        log.info("Updating payment status: {} to {}", orderId, paymentStatus);
        
        Order order = getOrderById(orderId);
        order.setPaymentStatus(paymentStatus);
        
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getOrderCountByStatus(Order.OrderStatus status) {
        log.info("Getting order count by status: {}", status);
        
        return orderRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalRevenueByStatusAndDateRange(Order.OrderStatus status, 
                                                    LocalDateTime startDate, 
                                                    LocalDateTime endDate) {
        log.info("Getting total revenue by status and date range: {} from {} to {}", status, startDate, endDate);
        
        Double result = orderRepository.sumTotalAmountByStatusAndDateRange(status, startDate, endDate);
        return result != null ? result : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getRecentOrdersByUserId(String userId, int limit) {
        log.info("Getting recent orders by user ID: {} with limit: {}", userId, limit);
        
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findRecentOrdersByUserId(userId, pageable);
    }
}
