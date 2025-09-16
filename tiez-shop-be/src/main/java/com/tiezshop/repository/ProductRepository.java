package com.tiezshop.repository;

import com.tiezshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
    
    Optional<Product> findBySku(String sku);
    
    Page<Product> findByIsActiveTrue(Pageable pageable);
    
    Page<Product> findByBrandIdAndIsActiveTrue(String brandId, Pageable pageable);
    
    Page<Product> findByCategoryIdAndIsActiveTrue(String categoryId, Pageable pageable);
    
    Page<Product> findByIsFeaturedTrueAndIsActiveTrue(Pageable pageable);
    
    List<Product> findTop10ByIsActiveTrueOrderByViewCountDesc();
    
    List<Product> findTop10ByIsActiveTrueOrderBySoldCountDesc();
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:brandId IS NULL OR p.brand.id = :brandId) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:gender IS NULL OR p.gender = :gender)")
    Page<Product> findProductsWithFilters(@Param("minPrice") BigDecimal minPrice,
                                        @Param("maxPrice") BigDecimal maxPrice,
                                        @Param("brandId") String brandId,
                                        @Param("categoryId") String categoryId,
                                        @Param("gender") String gender,
                                        Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.originalPrice IS NOT NULL AND p.originalPrice > p.price")
    Page<Product> findProductsOnSale(Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand.id = :brandId AND p.isActive = true")
    Long countActiveProductsByBrandId(@Param("brandId") String brandId);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.isActive = true")
    Long countActiveProductsByCategoryId(@Param("categoryId") String categoryId);
    
    boolean existsBySku(String sku);
}
