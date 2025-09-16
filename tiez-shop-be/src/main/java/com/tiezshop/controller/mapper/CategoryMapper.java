package com.tiezshop.controller.mapper;


import com.tiezshop.controller.dto.request.CategoryRequest;

import com.tiezshop.controller.dto.response.CategoryResponse;

import com.tiezshop.entity.Category;
import org.mapstruct.Mapper;

import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    
    Category toEntity(CategoryRequest categoryRequest);
    
    CategoryResponse toDto(Category category);

}
