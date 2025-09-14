package com.tiezshop.controller.mapper;

import com.tiezshop.controller.dto.request.RegisterRequest;
import com.tiezshop.controller.dto.request.UpdateUserRequest;
import com.tiezshop.controller.dto.response.UserResponse;
import com.tiezshop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toEntity(RegisterRequest registerRequest);
    UserResponse toDto(User user);
    @Mapping(target = "id", ignore = true)
    void updateUser(@MappingTarget User user, UpdateUserRequest request);
}
