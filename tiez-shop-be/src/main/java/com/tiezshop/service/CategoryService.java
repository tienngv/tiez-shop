package com.tiezshop.service;

import com.tiezshop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    
    Category createCategory(Category category);
    
    Category getCategoryById(String categoryId);
    
    Page<Category> getAllCategories(Pageable pageable);
    
    List<Category> getAllActiveCategories();
    
    List<Category> getRootCategories();
    
    List<Category> getSubCategories(String categoryId);
    
    List<Category> searchCategoriesByName(String name);
    
    Category updateCategory(String categoryId, Category category);
    
    void deleteCategory(String categoryId);
    
    Long getProductCountByCategoryId(String categoryId);
}