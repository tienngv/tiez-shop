package com.tiezshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product_images")
public class ProductImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Image URL is required")
    @Size(max = 1024, message = "Image URL must not exceed 1024 characters")
    @Column(nullable = false, length = 1024)
    private String imageUrl;

    @Size(max = 200, message = "Alt text must not exceed 200 characters")
    @Column(length = 200)
    private String altText;

    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Boolean isPrimary = false;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private Boolean isActive = true;
}
