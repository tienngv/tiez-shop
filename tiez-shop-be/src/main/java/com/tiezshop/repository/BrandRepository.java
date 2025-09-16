package com.tiezshop.repository;

import com.tiezshop.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String>, JpaSpecificationExecutor<Brand> {
    
    Optional<Brand> findByName(String name);
    
    List<Brand> findByIsActiveTrue();
    
    List<Brand> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT b FROM Brand b WHERE b.isActive = true ORDER BY b.name ASC")
    List<Brand> findAllActiveBrandsOrderByName();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand.id = :brandId")
    Long countProductsByBrandId(@Param("brandId") String brandId);
    
    boolean existsByName(String name);
}
