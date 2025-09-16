package com.tiezshop.repository;

import com.tiezshop.entity.Inventory;
import com.tiezshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String>, JpaSpecificationExecutor<Inventory> {
    
    List<Inventory> findByProductId(String productId);
    
    List<Inventory> findByProductIdAndIsActiveTrue(String productId);
    
    Optional<Inventory> findByProductIdAndSizeAndColor(String productId, String size, String color);
    
    List<Inventory> findByProductIdAndSize(String productId, String size);
    
    List<Inventory> findByProductIdAndColor(String productId, String color);
    
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.isActive = true AND i.quantity > 0")
    List<Inventory> findAvailableInventoriesByProductId(@Param("productId") String productId);
    
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.isActive = true AND i.quantity > i.reservedQuantity")
    List<Inventory> findInStockInventoriesByProductId(@Param("productId") String productId);
    
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.isActive = true ORDER BY i.size ASC")
    List<Inventory> findInventoriesByProductIdOrderBySize(@Param("productId") String productId);
    
    @Query("SELECT DISTINCT i.size FROM Inventory i WHERE i.product.id = :productId AND i.isActive = true AND i.quantity > 0 ORDER BY i.size ASC")
    List<String> findAvailableSizesByProductId(@Param("productId") String productId);
    
    @Query("SELECT DISTINCT i.color FROM Inventory i WHERE i.product.id = :productId AND i.isActive = true AND i.quantity > 0 ORDER BY i.color ASC")
    List<String> findAvailableColorsByProductId(@Param("productId") String productId);
    
    @Query("SELECT SUM(i.quantity) FROM Inventory i WHERE i.product.id = :productId AND i.isActive = true")
    Integer getTotalQuantityByProductId(@Param("productId") String productId);
    
    @Query("SELECT SUM(i.reservedQuantity) FROM Inventory i WHERE i.product.id = :productId AND i.isActive = true")
    Integer getTotalReservedQuantityByProductId(@Param("productId") String productId);
}
