package com.tshop.service;

import com.tshop.controller.dto.request.RoleRequest;
import com.tshop.controller.dto.response.DataResponse;


public interface RoleService {
    DataResponse saveRole(RoleRequest request);
}
