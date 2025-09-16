package com.tiezshop.controller.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    private String id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal discountPercentage;
    private Boolean isOnSale;
    
    private BrandResponse brand;
    private CategoryResponse category;
    
    private String color;
    private String size;
    private String material;
    private String gender;
    
    private Boolean isActive;
    private Boolean isFeatured;
    private Integer viewCount;
    private Integer soldCount;
    
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    
    // Nested response classes
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandResponse {
        private String id;
        private String name;
        private String logoUrl;
        private String country;
    }
    
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryResponse {
        private String id;
        private String name;
        private String description;
    }
}
