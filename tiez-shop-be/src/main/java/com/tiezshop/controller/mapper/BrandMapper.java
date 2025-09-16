package com.tiezshop.controller.mapper;

import com.tiezshop.controller.dto.request.BrandRequest;
import com.tiezshop.controller.dto.response.BrandResponse;
import com.tiezshop.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BrandMapper {
    
    Brand toEntity(BrandRequest brandRequest);
    
    BrandResponse toDto(Brand brand);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateBrand(@MappingTarget Brand brand, BrandRequest brandRequest);
}
