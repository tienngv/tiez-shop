package com.tiezshop.service;

import com.tiezshop.controller.dto.request.RoleRequest;
import com.tiezshop.controller.dto.response.DataResponse;


public interface RoleService {
    DataResponse saveRole(RoleRequest request);
}
