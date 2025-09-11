package com.tiezshop.controller.mapper;

import com.tiezshop.controller.dto.request.RegisterRequest;
import com.tiezshop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toEntity(RegisterRequest registerRequest);
}
