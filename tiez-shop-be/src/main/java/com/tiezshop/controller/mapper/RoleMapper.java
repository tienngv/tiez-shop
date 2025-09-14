package com.tiezshop.controller.mapper;

import com.tiezshop.controller.dto.request.RoleDto;
import com.tiezshop.entity.Role;
import org.mapstruct.Mapper;

import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {
    RoleDto toDto(Role role);
    Role toEntity(RoleDto roleDto);
}
