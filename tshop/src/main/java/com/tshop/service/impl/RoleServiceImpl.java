package com.tshop.service.impl;

import com.tshop.constrain.ErrorConst;
import com.tshop.controller.dto.request.RoleRequest;
import com.tshop.controller.dto.response.DataResponse;
import com.tshop.entity.Role;
import com.tshop.repository.RoleRepository;
import com.tshop.service.RoleService;
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
