package com.tiezshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "inventories")
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id;

    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotBlank(message = "Size is required")
    @Size(max = 20, message = "Size must not exceed 20 characters")
    @Column(nullable = false, length = 20)
    private String size;

    @NotBlank(message = "Color is required")
    @Size(max = 50, message = "Color must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String color;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be zero or positive")
    @Column(nullable = false)
    private Integer quantity = 0;

    @NotNull(message = "Reserved quantity is required")
    @PositiveOrZero(message = "Reserved quantity must be zero or positive")
    @Column(nullable = false)
    private Integer reservedQuantity = 0;

    @Column(nullable = false)
    private Boolean isActive = true;

    // Helper methods
    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public boolean isInStock() {
        return getAvailableQuantity() > 0;
    }

    public boolean canReserve(Integer requestedQuantity) {
        return getAvailableQuantity() >= requestedQuantity;
    }
}
