package com.tiezshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number must not exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;

    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Total amount is required")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @NotNull(message = "Order status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Column(length = 500)
    private String notes;

    // Shipping information
    @NotBlank(message = "Shipping name is required")
    @Size(max = 100, message = "Shipping name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String shippingName;

    @NotBlank(message = "Shipping phone is required")
    @Size(max = 20, message = "Shipping phone must not exceed 20 characters")
    @Column(nullable = false, length = 20)
    private String shippingPhone;

    @NotBlank(message = "Shipping address is required")
    @Size(max = 500, message = "Shipping address must not exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String shippingAddress;

    @Size(max = 100, message = "Shipping city must not exceed 100 characters")
    @Column(length = 100)
    private String shippingCity;

    @Size(max = 100, message = "Shipping district must not exceed 100 characters")
    @Column(length = 100)
    private String shippingDistrict;

    @Size(max = 10, message = "Shipping postal code must not exceed 10 characters")
    @Column(length = 10)
    private String shippingPostalCode;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    public enum OrderStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURNED
    }

    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED, PARTIALLY_REFUNDED
    }

    // Helper methods
    public BigDecimal getFinalAmount() {
        return totalAmount.subtract(discountAmount).add(shippingFee).add(taxAmount);
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    public boolean isCompleted() {
        return status == OrderStatus.DELIVERED;
    }
}
