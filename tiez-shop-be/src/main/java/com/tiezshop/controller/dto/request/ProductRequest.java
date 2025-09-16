package com.tiezshop.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    
    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    @NotBlank(message = "Brand ID is required")
    private String brandId;
    
    @NotBlank(message = "Category ID is required")
    private String categoryId;
    
    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;
    
    @Size(max = 20, message = "Size must not exceed 20 characters")
    private String size;
    
    @Size(max = 50, message = "Material must not exceed 50 characters")
    private String material;
    
    @Size(max = 100, message = "Gender must not exceed 100 characters")
    private String gender;
    
    private Boolean isActive = true;
    
    private Boolean isFeatured = false;
}
