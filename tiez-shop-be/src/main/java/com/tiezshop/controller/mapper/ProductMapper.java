package com.tiezshop.controller.mapper;

import com.tiezshop.controller.dto.request.ProductRequest;
import com.tiezshop.controller.dto.response.ProductResponse;
import com.tiezshop.entity.Brand;
import com.tiezshop.entity.Category;
import com.tiezshop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    
    @Mapping(target = "brand", source = "brandId", qualifiedByName = "mapBrandIdToBrand")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategoryIdToCategory")
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "soldCount", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "inventories", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Product toEntity(ProductRequest productRequest);
    
    ProductResponse toDto(Product product);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "soldCount", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "inventories", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "brand", source = "brandId", qualifiedByName = "mapBrandIdToBrand")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategoryIdToCategory")
    void updateProduct(@MappingTarget Product product, ProductRequest productRequest);
    
    @org.mapstruct.Named("mapBrandIdToBrand")
    default Brand mapBrandIdToBrand(String brandId) {
        if (brandId == null) {
            return null;
        }
        return Brand.builder().id(brandId).build();
    }
    
    @org.mapstruct.Named("mapCategoryIdToCategory")
    default Category mapCategoryIdToCategory(String categoryId) {
        if (categoryId == null) {
            return null;
        }
        return Category.builder().id(categoryId).build();
    }
}
