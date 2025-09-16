package com.tiezshop.service.impl;

import com.tiezshop.constrain.ErrorConst;
import com.tiezshop.controller.dto.request.ProductRequest;
import com.tiezshop.controller.dto.response.ProductResponse;
import com.tiezshop.controller.mapper.ProductMapper;
import com.tiezshop.entity.Brand;
import com.tiezshop.entity.Category;
import com.tiezshop.entity.Product;
import com.tiezshop.exception.AppException;
import com.tiezshop.repository.BrandRepository;
import com.tiezshop.repository.CategoryRepository;
import com.tiezshop.repository.ProductRepository;
import com.tiezshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Creating product: {}", productRequest.getName());
        
        // Validate brand exists
        Brand brand = brandRepository.findById(productRequest.getBrandId())
                .orElseThrow(() -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(), 
                    "Không tìm thấy thương hiệu với ID: " + productRequest.getBrandId()));
        
        // Validate category exists
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(), 
                    "Không tìm thấy danh mục với ID: " + productRequest.getCategoryId()));
        
        // Check SKU uniqueness
        if (productRepository.existsBySku(productRequest.getSku())) {
            throw new AppException(ErrorConst.CONFLICT.getErrCode(), 
                "SKU '" + productRequest.getSku() + "' đã tồn tại");
        }
        
        // Convert request to entity
        Product product = productMapper.toEntity(productRequest);
        
        // Set default values
        if (product.getViewCount() == null) {
            product.setViewCount(0);
        }
        if (product.getSoldCount() == null) {
            product.setSoldCount(0);
        }
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }
        if (product.getIsFeatured() == null) {
            product.setIsFeatured(false);
        }
        
        product.setBrand(brand);
        product.setCategory(category);
        
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        
        return productMapper.toDto(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(String productId, ProductRequest productRequest) {
        log.info("Updating product: {}", productId);
        
        Product existingProduct = getProductById(productId);
        
        // Validate brand exists if changed
        if (productRequest.getBrandId() != null && !productRequest.getBrandId().equals(existingProduct.getBrand().getId())) {
            Brand brand = brandRepository.findById(productRequest.getBrandId())
                    .orElseThrow(() -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(), 
                        "Không tìm thấy thương hiệu với ID: " + productRequest.getBrandId()));
            existingProduct.setBrand(brand);
        }
        
        // Validate category exists if changed
        if (productRequest.getCategoryId() != null && !productRequest.getCategoryId().equals(existingProduct.getCategory().getId())) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(), 
                        "Không tìm thấy danh mục với ID: " + productRequest.getCategoryId()));
            existingProduct.setCategory(category);
        }
        
        // Check SKU uniqueness if changed
        if (productRequest.getSku() != null && !productRequest.getSku().equals(existingProduct.getSku())) {
            if (productRepository.existsBySku(productRequest.getSku())) {
                throw new AppException(ErrorConst.CONFLICT.getErrCode(), 
                    "SKU '" + productRequest.getSku() + "' đã tồn tại");
            }
        }
        
        // Update product using mapper
        productMapper.updateProduct(existingProduct, productRequest);
        
        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product updated successfully: {}", productId);
        
        return productMapper.toDto(updatedProduct);
    }

    @Override
    public void deleteProduct(String productId) {
        log.info("Deleting product: {}", productId);
        
        Product product = getProductById(productId);
        
        // Soft delete by setting isActive to false
        product.setIsActive(false);
        productRepository.save(product);
        
        log.info("Product deleted successfully: {}", productId);
    }

    @Override
    public ProductResponse getProductById(String productId) {
        log.info("Getting product by ID: {}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(), 
                    "Không tìm thấy sản phẩm với ID: " + productId));
        
        return productMapper.toDto(product);
    }

    @Override
    public ProductResponse getProductBySku(String sku) {
        log.info("Getting product by SKU: {}", sku);
        
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(), 
                    "Không tìm thấy sản phẩm với SKU: " + sku));
        
        return productMapper.toDto(product);
    }

    @Override
    public Page<ProductResponse> getAllActiveProducts(Pageable pageable) {
        log.info("Getting all active products with pagination");
        
        Page<Product> products = productRepository.findByIsActiveTrue(pageable);
        return products.map(productMapper::toDto);
    }

    @Override
    public Page<ProductResponse> getProductsByBrand(String brandId, Pageable pageable) {
        log.info("Getting products by brand: {}", brandId);
        
        Page<Product> products = productRepository.findByBrandIdAndIsActiveTrue(brandId, pageable);
        return products.map(productMapper::toDto);
    }

    @Override
    public Page<ProductResponse> getProductsByCategory(String categoryId, Pageable pageable) {
        log.info("Getting products by category: {}", categoryId);
        
        Page<Product> products = productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
        return products.map(productMapper::toDto);
    }

    @Override
    public Page<ProductResponse> getFeaturedProducts(Pageable pageable) {
        log.info("Getting featured products");
        
        Page<Product> products = productRepository.findByIsFeaturedTrueAndIsActiveTrue(pageable);
        return products.map(productMapper::toDto);
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        log.info("Searching products with keyword: {}", keyword);
        
        Page<Product> products = productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword, pageable);
        return products.map(productMapper::toDto);
    }

    @Override
    public Page<ProductResponse> getProductsWithFilters(BigDecimal minPrice, BigDecimal maxPrice, 
                                      String brandId, String categoryId, 
                                      String gender, Pageable pageable) {
        log.info("Getting products with filters - minPrice: {}, maxPrice: {}, brandId: {}, categoryId: {}, gender: {}", 
                minPrice, maxPrice, brandId, categoryId, gender);
        
        Page<Product> products = productRepository.findProductsWithFilters(minPrice, maxPrice, brandId, categoryId, gender, pageable);
        return products.map(productMapper::toDto);
    }

    @Override
    public Page<ProductResponse> getProductsOnSale(Pageable pageable) {
        log.info("Getting products on sale");
        
        Page<Product> products = productRepository.findProductsOnSale(pageable);
        return products.map(productMapper::toDto);
    }

    @Override
    public List<ProductResponse> getTopViewedProducts(int limit) {
        log.info("Getting top {} viewed products", limit);
        
        List<Product> products = productRepository.findTop10ByIsActiveTrueOrderByViewCountDesc();
        return products.stream()
                .limit(limit)
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getTopSellingProducts(int limit) {
        log.info("Getting top {} selling products", limit);
        
        List<Product> products = productRepository.findTop10ByIsActiveTrueOrderBySoldCountDesc();
        return products.stream()
                .limit(limit)
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void incrementViewCount(String productId) {
        log.info("Incrementing view count for product: {}", productId);
        
        ProductRe product = getProductById(productId);
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);
    }

    @Override
    public void incrementSoldCount(String productId, int quantity) {
        log.info("Incrementing sold count for product: {} by quantity: {}", productId, quantity);
        
        Product product = getProductById(productId);
        product.setSoldCount(product.getSoldCount() + quantity);
        productRepository.save(product);
    }

    @Override
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }

    @Override
    public Long getProductCountByBrandId(String brandId) {
        log.info("Getting product count for brand: {}", brandId);
        return productRepository.countByBrandIdAndIsActiveTrue(brandId);
    }

    @Override
    public Long getProductCountByCategoryId(String categoryId) {
        log.info("Getting product count for category: {}", categoryId);
        return productRepository.countByCategoryIdAndIsActiveTrue(categoryId);
    }

}