package com.tiezshop.service;

import com.tiezshop.controller.dto.request.BrandRequest;
import com.tiezshop.controller.dto.response.BrandResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BrandService {
    
    BrandResponse createBrand(BrandRequest brandRequest);
    
    BrandResponse updateBrand(String brandId, BrandRequest brandRequest);
    
    void deleteBrand(String brandId);
    
    BrandResponse getBrandById(String brandId);
    
    BrandResponse getBrandByName(String name);
    
    List<BrandResponse> getAllActiveBrands();
    
    Page<BrandResponse> getAllBrands(Pageable pageable);
    
    List<BrandResponse> searchBrandsByName(String name);
    
    boolean existsByName(String name);
    
    Long getProductCountByBrandId(String brandId);
}
