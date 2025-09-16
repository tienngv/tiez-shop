package com.tiezshop.controller;

import com.tiezshop.controller.dto.request.BrandRequest;
import com.tiezshop.controller.dto.response.BrandResponse;
import com.tiezshop.controller.dto.response.DataResponse;
import com.tiezshop.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Log4j2
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "Tạo thương hiệu mới", tags = "API - [BRAND]")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public DataResponse<BrandResponse> createBrand(@Valid @RequestBody BrandRequest request) {
        log.info("Creating brand: {}", request.getName());
        
        BrandResponse response = brandService.createBrand(request);
        
        return DataResponse.<BrandResponse>builder()
                .result(response)
                .message("Tạo thương hiệu thành công")
                .build();
    }

    @Operation(summary = "Lấy thông tin thương hiệu theo ID", tags = "API - [BRAND]")
    @GetMapping("/{brandId}")
    public DataResponse<BrandResponse> getBrandById(@PathVariable String brandId) {
        log.info("Getting brand by ID: {}", brandId);
        
        BrandResponse response = brandService.getBrandById(brandId);
        
        return DataResponse.<BrandResponse>builder()
                .result(response)
                .message("Lấy thông tin thương hiệu thành công")
                .build();
    }

    @Operation(summary = "Lấy danh sách thương hiệu có phân trang", tags = "API - [BRAND]")
    @GetMapping
    public DataResponse<Page<BrandResponse>> getAllBrands(Pageable pageable) {
        log.info("Getting all brands with pagination");
        
        Page<BrandResponse> response = brandService.getAllBrands(pageable);
        
        return DataResponse.<Page<BrandResponse>>builder()
                .result(response)
                .message("Lấy danh sách thương hiệu thành công")
                .build();
    }

    @Operation(summary = "Lấy danh sách thương hiệu đang hoạt động", tags = "API - [BRAND]")
    @GetMapping("/active")
    public DataResponse<List<BrandResponse>> getAllActiveBrands() {
        log.info("Getting all active brands");
        
        List<BrandResponse> response = brandService.getAllActiveBrands();
        
        return DataResponse.<List<BrandResponse>>builder()
                .result(response)
                .message("Lấy danh sách thương hiệu đang hoạt động thành công")
                .build();
    }

    @Operation(summary = "Tìm kiếm thương hiệu theo tên", tags = "API - [BRAND]")
    @GetMapping("/search")
    public DataResponse<List<BrandResponse>> searchBrands(@RequestParam String name) {
        log.info("Searching brands by name: {}", name);
        
        List<BrandResponse> response = brandService.searchBrandsByName(name);
        
        return DataResponse.<List<BrandResponse>>builder()
                .result(response)
                .message("Tìm kiếm thương hiệu thành công")
                .build();
    }

    @Operation(summary = "Cập nhật thương hiệu", tags = "API - [BRAND]")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{brandId}")
    public DataResponse<BrandResponse> updateBrand(@PathVariable String brandId, 
                                                   @Valid @RequestBody BrandRequest request) {
        log.info("Updating brand: {}", brandId);
        
        BrandResponse response = brandService.updateBrand(brandId, request);
        
        return DataResponse.<BrandResponse>builder()
                .result(response)
                .message("Cập nhật thương hiệu thành công")
                .build();
    }

    @Operation(summary = "Xóa thương hiệu", tags = "API - [BRAND]")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{brandId}")
    public DataResponse<Void> deleteBrand(@PathVariable String brandId) {
        log.info("Deleting brand: {}", brandId);
        
        brandService.deleteBrand(brandId);
        
        return DataResponse.<Void>builder()
                .message("Xóa thương hiệu thành công")
                .build();
    }

    @Operation(summary = "Lấy số lượng sản phẩm theo thương hiệu", tags = "API - [BRAND]")
    @GetMapping("/{brandId}/product-count")
    public DataResponse<Long> getProductCountByBrand(@PathVariable String brandId) {
        log.info("Getting product count for brand: {}", brandId);
        
        Long count = brandService.getProductCountByBrandId(brandId);
        
        return DataResponse.<Long>builder()
                .result(count)
                .message("Lấy số lượng sản phẩm thành công")
                .build();
    }
}