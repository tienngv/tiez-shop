package com.tiezshop.service.impl;

import com.tiezshop.entity.Category;
import com.tiezshop.repository.CategoryRepository;
import com.tiezshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        log.info("Creating category: {}", category.getName());
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(String categoryId) {
        log.info("Getting category by ID: {}", categoryId);
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
    }

    @Override
    public Page<Category> getAllCategories(Pageable pageable) {
        log.info("Getting all categories with pagination");
        return categoryRepository.findAll(pageable);
    }

    @Override
    public List<Category> getAllActiveCategories() {
        log.info("Getting all active categories");
        return categoryRepository.findByIsActiveTrue();
    }

    @Override
    public List<Category> getRootCategories() {
        log.info("Getting root categories");
        return categoryRepository.findByParentCategoryIsNullAndIsActiveTrue();
    }

    @Override
    public List<Category> getSubCategories(String categoryId) {
        log.info("Getting subcategories for category: {}", categoryId);
        Category parentCategory = getCategoryById(categoryId);
        return categoryRepository.findByParentCategoryAndIsActiveTrue(parentCategory);
    }

    @Override
    public List<Category> searchCategoriesByName(String name) {
        log.info("Searching categories by name: {}", name);
        return categoryRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name);
    }

    @Override
    public Category updateCategory(String categoryId, Category category) {
        log.info("Updating category: {}", categoryId);
        Category existingCategory = getCategoryById(categoryId);
        
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setImageUrl(category.getImageUrl());
        existingCategory.setIsActive(category.getIsActive());
        
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(String categoryId) {
        log.info("Deleting category: {}", categoryId);
        Category category = getCategoryById(categoryId);
        categoryRepository.delete(category);
    }

    @Override
    public Long getProductCountByCategoryId(String categoryId) {
        log.info("Getting product count for category: {}", categoryId);
        Category category = getCategoryById(categoryId);
        return categoryRepository.countProductsByCategory(category);
    }
}
