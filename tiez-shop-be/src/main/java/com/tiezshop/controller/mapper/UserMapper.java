package com.tiezshop.controller.mapper;

import com.tiezshop.controller.dto.request.UserRequest;
import com.tiezshop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserRequest request);
    UserRequest toDto(User user);
}
