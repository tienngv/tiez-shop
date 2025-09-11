package com.tiezshop.controller.mapper;

import com.tiezshop.controller.dto.request.RegisterRequest;
import com.tiezshop.controller.dto.response.UserResponse;
import com.tiezshop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toEntity(RegisterRequest registerRequest);
    UserResponse toDto(User user);
}
