package com.tiezshop.service;

import com.tiezshop.controller.dto.request.LoginRequest;
import com.tiezshop.controller.dto.request.UserRequest;
import com.tiezshop.controller.dto.response.DataResponse;

public interface AuthService {
    DataResponse register(UserRequest request);
    DataResponse login(LoginRequest request);
}
