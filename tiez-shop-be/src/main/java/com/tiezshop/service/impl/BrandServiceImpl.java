package com.tiezshop.service.impl;

import com.tiezshop.constrain.ErrorConst;
import com.tiezshop.controller.dto.request.BrandRequest;
import com.tiezshop.controller.dto.response.BrandResponse;
import com.tiezshop.controller.mapper.BrandMapper;
import com.tiezshop.entity.Brand;
import com.tiezshop.exception.AppException;
import com.tiezshop.repository.BrandRepository;
import com.tiezshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public BrandResponse createBrand(BrandRequest brandRequest) {
        log.info("Creating brand: {}", brandRequest.getName());

        // Check if brand name already exists
        if (brandRepository.existsByName(brandRequest.getName())) {
            throw new AppException(ErrorConst.CONFLICT.getErrCode(),
                    "Thương hiệu '" + brandRequest.getName() + "' đã tồn tại");
        }

        // Convert request to entity
        Brand brand = brandMapper.toEntity(brandRequest);

        // Set default values
        if (brand.getIsActive() == null) {
            brand.setIsActive(true);
        }

        Brand savedBrand = brandRepository.save(brand);
        log.info("Brand created successfully with ID: {}", savedBrand.getId());

        return brandMapper.toDto(savedBrand);
    }

    @Override
    public BrandResponse updateBrand(String brandId, BrandRequest brandRequest) {
        log.info("Updating brand: {}", brandId);

        Brand existingBrand = getBrandById(brandId);

        // Check if name is being changed and if new name already exists
        if (!existingBrand.getName().equals(brandRequest.getName()) &&
                brandRepository.existsByName(brandRequest.getName())) {
            throw new AppException(ErrorConst.CONFLICT.getErrCode(),
                    "Thương hiệu '" + brandRequest.getName() + "' đã tồn tại");
        }

        // Update brand using mapper
        brandMapper.updateBrand(existingBrand, brandRequest);

        Brand updatedBrand = brandRepository.save(existingBrand);
        log.info("Brand updated successfully: {}", brandId);

        return brandMapper.toDto(updatedBrand);
    }

    @Override
    public void deleteBrand(String brandId) {
        log.info("Deleting brand: {}", brandId);

        Brand brand = getBrandById(brandId);

        // Check if brand has products
        Long productCount = getProductCountByBrandId(brandId);
        if (productCount > 0) {
            throw new AppException(ErrorConst.CONFLICT.getErrCode(),
                    "Không thể xóa thương hiệu có " + productCount + " sản phẩm");
        }

        brandRepository.delete(brand);
        log.info("Brand deleted successfully: {}", brandId);
    }

    @Override
    public BrandResponse getBrandById(String brandId) {
        log.info("Getting brand by ID: {}", brandId);

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(),
                        "Không tìm thấy thương hiệu với ID: " + brandId));

        return brandMapper.toDto(brand);
    }

    @Override
    public BrandResponse getBrandByName(String name) {
        log.info("Getting brand by name: {}", name);

        Brand brand = brandRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(),
                        "Không tìm thấy thương hiệu với tên: " + name));

        return brandMapper.toDto(brand);
    }

    @Override
    public List<BrandResponse> getAllActiveBrands() {
        log.info("Getting all active brands");

        List<Brand> brands = brandRepository.findByIsActiveTrue();
        return brands.stream()
                .map(brandMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BrandResponse> getAllBrands(Pageable pageable) {
        log.info("Getting all brands with pagination");

        Page<Brand> brands = brandRepository.findAll(pageable);
        return brands.map(brandMapper::toDto);
    }

    @Override
    public List<BrandResponse> searchBrandsByName(String name) {
        log.info("Searching brands by name: {}", name);

        if (name == null || name.trim().isEmpty()) {
            return getAllActiveBrands();
        }

        List<Brand> brands = brandRepository.findByNameContainingIgnoreCase(name.trim());
        return brands.stream()
                .map(brandMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return brandRepository.existsByName(name);
    }

    @Override
    public Long getProductCountByBrandId(String brandId) {
        log.info("Getting product count for brand: {}", brandId);
        return brandRepository.countProductsByBrandId(brandId);
    }

}