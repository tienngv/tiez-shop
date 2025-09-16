package com.tiezshop.controller;

import com.tiezshop.controller.dto.request.CategoryRequest;
import com.tiezshop.controller.dto.response.CategoryResponse;
import com.tiezshop.controller.dto.response.DataResponse;
import com.tiezshop.controller.mapper.CategoryMapper;
import com.tiezshop.entity.Category;
import com.tiezshop.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Log4j2
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Operation(summary = "Tạo danh mục mới", tags = "API - [CATEGORY]")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public DataResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("Creating category: {}", request.getName());
        
        Category category = categoryMapper.toEntity(request);
        Category createdCategory = categoryService.createCategory(category);
        CategoryResponse response = categoryMapper.toDto(createdCategory);
        
        return DataResponse.<CategoryResponse>builder()
                .result(response)
                .message("Tạo danh mục thành công")
                .build();
    }

    @Operation(summary = "Lấy thông tin danh mục theo ID", tags = "API - [CATEGORY]")
    @GetMapping("/{categoryId}")
    public DataResponse<CategoryResponse> getCategoryById(@PathVariable String categoryId) {
        log.info("Getting category by ID: {}", categoryId);
        
        Category category = categoryService.getCategoryById(categoryId);
        CategoryResponse response = categoryMapper.toDto(category);
        
        return DataResponse.<CategoryResponse>builder()
                .result(response)
                .message("Lấy thông tin danh mục thành công")
                .build();
    }

    @Operation(summary = "Lấy danh sách danh mục có phân trang", tags = "API - [CATEGORY]")
    @GetMapping
    public DataResponse<Page<CategoryResponse>> getAllCategories(Pageable pageable) {
        log.info("Getting all categories with pagination");
        
        Page<Category> categories = categoryService.getAllCategories(pageable);
        Page<CategoryResponse> response = categories.map(categoryMapper::toDto);
        
        return DataResponse.<Page<CategoryResponse>>builder()
                .result(response)
                .message("Lấy danh sách danh mục thành công")
                .build();
    }

    @Operation(summary = "Lấy danh sách danh mục đang hoạt động", tags = "API - [CATEGORY]")
    @GetMapping("/active")
    public DataResponse<List<CategoryResponse>> getAllActiveCategories() {
        log.info("Getting all active categories");
        
        List<Category> categories = categoryService.getAllActiveCategories();
        List<CategoryResponse> response = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        
        return DataResponse.<List<CategoryResponse>>builder()
                .result(response)
                .message("Lấy danh sách danh mục đang hoạt động thành công")
                .build();
    }

    @Operation(summary = "Lấy danh sách danh mục gốc", tags = "API - [CATEGORY]")
    @GetMapping("/root")
    public DataResponse<List<CategoryResponse>> getRootCategories() {
        log.info("Getting root categories");
        
        List<Category> categories = categoryService.getRootCategories();
        List<CategoryResponse> response = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        
        return DataResponse.<List<CategoryResponse>>builder()
                .result(response)
                .message("Lấy danh sách danh mục gốc thành công")
                .build();
    }

    @Operation(summary = "Lấy danh sách danh mục con", tags = "API - [CATEGORY]")
    @GetMapping("/{categoryId}/subcategories")
    public DataResponse<List<CategoryResponse>> getSubCategories(@PathVariable String categoryId) {
        log.info("Getting subcategories for category: {}", categoryId);
        
        List<Category> categories = categoryService.getSubCategories(categoryId);
        List<CategoryResponse> response = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        
        return DataResponse.<List<CategoryResponse>>builder()
                .result(response)
                .message("Lấy danh sách danh mục con thành công")
                .build();
    }

    @Operation(summary = "Tìm kiếm danh mục theo tên", tags = "API - [CATEGORY]")
    @GetMapping("/search")
    public DataResponse<List<CategoryResponse>> searchCategories(@RequestParam String name) {
        log.info("Searching categories by name: {}", name);
        
        List<Category> categories = categoryService.searchCategoriesByName(name);
        List<CategoryResponse> response = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        
        return DataResponse.<List<CategoryResponse>>builder()
                .result(response)
                .message("Tìm kiếm danh mục thành công")
                .build();
    }

    @Operation(summary = "Cập nhật danh mục", tags = "API - [CATEGORY]")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{categoryId}")
    public DataResponse<CategoryResponse> updateCategory(@PathVariable String categoryId, 
                                                         @Valid @RequestBody CategoryRequest request) {
        log.info("Updating category: {}", categoryId);
        
        Category category = categoryMapper.toEntity(request);
        Category updatedCategory = categoryService.updateCategory(categoryId, category);
        CategoryResponse response = categoryMapper.toDto(updatedCategory);
        
        return DataResponse.<CategoryResponse>builder()
                .result(response)
                .message("Cập nhật danh mục thành công")
                .build();
    }

    @Operation(summary = "Xóa danh mục", tags = "API - [CATEGORY]")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{categoryId}")
    public DataResponse<Void> deleteCategory(@PathVariable String categoryId) {
        log.info("Deleting category: {}", categoryId);
        
        categoryService.deleteCategory(categoryId);
        
        return DataResponse.<Void>builder()
                .message("Xóa danh mục thành công")
                .build();
    }

    @Operation(summary = "Lấy số lượng sản phẩm theo danh mục", tags = "API - [CATEGORY]")
    @GetMapping("/{categoryId}/product-count")
    public DataResponse<Long> getProductCountByCategory(@PathVariable String categoryId) {
        log.info("Getting product count for category: {}", categoryId);
        
        Long count = categoryService.getProductCountByCategoryId(categoryId);
        
        return DataResponse.<Long>builder()
                .result(count)
                .message("Lấy số lượng sản phẩm thành công")
                .build();
    }
}
