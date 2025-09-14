package com.tiezshop.service;

import com.tiezshop.controller.dto.request.RoleDto;


import java.util.List;

public interface RoleService {
    String createRole(RoleDto request);
    List<RoleDto> getAll();

}
