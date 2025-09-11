package com.tiezshop.service.impl;

import com.tiezshop.constrain.ErrorConst;
import com.tiezshop.controller.dto.request.RoleRequest;
import com.tiezshop.controller.dto.response.DataResponse;
import com.tiezshop.entity.Role;
import com.tiezshop.repository.RoleRepository;
import com.tiezshop.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public DataResponse saveRole(RoleRequest request) {
        Role role = Role.builder().
                code(request.getCode()).
                description(request.getDescription()).
                build();
        role = roleRepository.save(role);
        return new DataResponse(ErrorConst.SUCCESS, role);
    }
}
