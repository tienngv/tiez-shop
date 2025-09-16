package com.tiezshop.service;

import com.tiezshop.controller.dto.request.ProductRequest;
import com.tiezshop.controller.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    
    ProductResponse createProduct(ProductRequest productRequest);
    
    ProductResponse updateProduct(String productId, ProductRequest productRequest);
    
    void deleteProduct(String productId);
    
    ProductResponse getProductById(String productId);
    
    ProductResponse getProductBySku(String sku);
    
    Page<ProductResponse> getAllActiveProducts(Pageable pageable);
    
    Page<ProductResponse> getProductsByBrand(String brandId, Pageable pageable);
    
    Page<ProductResponse> getProductsByCategory(String categoryId, Pageable pageable);
    
    Page<ProductResponse> getFeaturedProducts(Pageable pageable);
    
    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);
    
    Page<ProductResponse> getProductsWithFilters(BigDecimal minPrice, BigDecimal maxPrice, 
                                      String brandId, String categoryId, 
                                      String gender, Pageable pageable);
    
    Page<ProductResponse> getProductsOnSale(Pageable pageable);
    
    List<ProductResponse> getTopViewedProducts(int limit);
    
    List<ProductResponse> getTopSellingProducts(int limit);
    
    void incrementViewCount(String productId);
    
    void incrementSoldCount(String productId, int quantity);
    
    boolean existsBySku(String sku);
    
    Long getProductCountByBrandId(String brandId);
    
    Long getProductCountByCategoryId(String categoryId);
}
