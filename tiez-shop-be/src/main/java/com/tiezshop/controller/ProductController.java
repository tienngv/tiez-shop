package com.tiezshop.controller;

import com.tiezshop.controller.dto.request.ProductRequest;
import com.tiezshop.controller.dto.response.DataResponse;
import com.tiezshop.controller.dto.response.ProductResponse;
import com.tiezshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Log4j2
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Tạo sản phẩm mới", tags = "API - [PRODUCT]")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public DataResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        log.info("Creating product: {}", request.getName());
        
        ProductResponse response = productService.createProduct(request);
        
        return DataResponse.<ProductResponse>builder()
                .result(response)
                .message("Tạo sản phẩm thành công")
                .build();
    }

    @Operation(summary = "Lấy thông tin sản phẩm theo ID", tags = "API - [PRODUCT]")
    @GetMapping("/{productId}")
    public DataResponse<ProductResponse> getProductById(@PathVariable String productId) {
        log.info("Getting product by ID: {}", productId);
        
        ProductResponse response = productService.getProductById(productId);
        
        // Increment view count
        productService.incrementViewCount(productId);
        
        return DataResponse.<ProductResponse>builder()
                .result(response)
                .message("Lấy thông tin sản phẩm thành công")
                .build();
    }

    @Operation(summary = "Lấy danh sách sản phẩm có phân trang", tags = "API - [PRODUCT]")
    @GetMapping
    public DataResponse<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        log.info("Getting all products with pagination");
        
        Page<ProductResponse> response = productService.getAllActiveProducts(pageable);
        
        return DataResponse.<Page<ProductResponse>>builder()
                .result(response)
                .message("Lấy danh sách sản phẩm thành công")
                .build();
    }

    @Operation(summary = "Lấy danh sách sản phẩm nổi bật", tags = "API - [PRODUCT]")
    @GetMapping("/featured")
    public DataResponse<Page<ProductResponse>> getFeaturedProducts(Pageable pageable) {
        log.info("Getting featured products");
        
        Page<ProductResponse> response = productService.getFeaturedProducts(pageable);
        
        return DataResponse.<Page<ProductResponse>>builder()
                .result(response)
                .message("Lấy danh sách sản phẩm nổi bật thành công")
                .build();
    }

    @Operation(summary = "Tìm kiếm sản phẩm", tags = "API - [PRODUCT]")
    @GetMapping("/search")
    public DataResponse<Page<ProductResponse>> searchProducts(@RequestParam String keyword, 
                                                               Pageable pageable) {
        log.info("Searching products with keyword: {}", keyword);
        
        Page<ProductResponse> response = productService.searchProducts(keyword, pageable);
        
        return DataResponse.<Page<ProductResponse>>builder()
                .result(response)
                .message("Tìm kiếm sản phẩm thành công")
                .build();
    }

    @Operation(summary = "Lọc sản phẩm theo điều kiện", tags = "API - [PRODUCT]")
    @GetMapping("/filter")
    public DataResponse<Page<ProductResponse>> getProductsWithFilters(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String brandId,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String gender,
            Pageable pageable) {
        
        log.info("Filtering products with filters: minPrice={}, maxPrice={}, brandId={}, categoryId={}, gender={}", 
                minPrice, maxPrice, brandId, categoryId, gender);
        
        Page<ProductResponse> response = productService.getProductsWithFilters(
                minPrice, maxPrice, brandId, categoryId, gender, pageable);
        
        return DataResponse.<Page<ProductResponse>>builder()
                .result(response)
                .message("Lọc sản phẩm thành công")
                .build();
    }

    @Operation(summary = "Lấy danh sách sản phẩm đang giảm giá", tags = "API - [PRODUCT]")
    @GetMapping("/on-sale")
    public DataResponse<Page<ProductResponse>> getProductsOnSale(Pageable pageable) {
        log.info("Getting products on sale");
        
        Page<ProductResponse> response = productService.getProductsOnSale(pageable);
        
        return DataResponse.<Page<ProductResponse>>builder()
                .result(response)
                .message("Lấy danh sách sản phẩm đang giảm giá thành công")
                .build();
    }

    @Operation(summary = "Lấy danh sách sản phẩm xem nhiều nhất", tags = "API - [PRODUCT]")
    @GetMapping("/top-viewed")
    public DataResponse<List<ProductResponse>> getTopViewedProducts(@RequestParam(defaultValue = "10") int limit) {
        log.info("Getting top viewed products with limit: {}", limit);
        
        List<ProductResponse> response = productService.getTopViewedProducts(limit);
        
        return DataResponse.<List<ProductResponse>>builder()
                .result(response)
                .message("Lấy danh sách sản phẩm xem nhiều nhất thành công")
                .build();
    }

    @Operation(summary = "Lấy danh sách sản phẩm bán chạy nhất", tags = "API - [PRODUCT]")
    @GetMapping("/top-selling")
    public DataResponse<List<ProductResponse>> getTopSellingProducts(@RequestParam(defaultValue = "10") int limit) {
        log.info("Getting top selling products with limit: {}", limit);
        
        List<ProductResponse> response = productService.getTopSellingProducts(limit);
        
        return DataResponse.<List<ProductResponse>>builder()
                .result(response)
                .message("Lấy danh sách sản phẩm bán chạy nhất thành công")
                .build();
    }

    @Operation(summary = "Cập nhật sản phẩm", tags = "API - [PRODUCT]")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{productId}")
    public DataResponse<ProductResponse> updateProduct(@PathVariable String productId, 
                                                       @Valid @RequestBody ProductRequest request) {
        log.info("Updating product: {}", productId);
        
        ProductResponse response = productService.updateProduct(productId, request);
        
        return DataResponse.<ProductResponse>builder()
                .result(response)
                .message("Cập nhật sản phẩm thành công")
                .build();
    }

    @Operation(summary = "Xóa sản phẩm", tags = "API - [PRODUCT]")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{productId}")
    public DataResponse<Void> deleteProduct(@PathVariable String productId) {
        log.info("Deleting product: {}", productId);
        
        productService.deleteProduct(productId);
        
        return DataResponse.<Void>builder()
                .message("Xóa sản phẩm thành công")
                .build();
    }
}