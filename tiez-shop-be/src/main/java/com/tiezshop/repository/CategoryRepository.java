package com.tiezshop.repository;

import com.tiezshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String>, JpaSpecificationExecutor<Category> {
    
    Optional<Category> findByName(String name);
    
    List<Category> findByIsActiveTrue();
    
    List<Category> findByParentCategoryIsNull();
    
    List<Category> findByParentCategoryId(String parentCategoryId);
    
    List<Category> findByNameContainingIgnoreCase(String name);
    
    // Methods for active categories
    List<Category> findByParentCategoryIsNullAndIsActiveTrue();
    
    List<Category> findByParentCategoryAndIsActiveTrue(Category parentCategory);
    
    List<Category> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND c.parentCategory IS NULL ORDER BY c.name ASC")
    List<Category> findAllRootCategories();
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND c.parentCategory.id = :parentId ORDER BY c.name ASC")
    List<Category> findSubCategoriesByParentId(@Param("parentId") String parentId);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category")
    Long countProductsByCategory(@Param("category") Category category);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    Long countProductsByCategoryId(@Param("categoryId") String categoryId);
    
    boolean existsByName(String name);
}
