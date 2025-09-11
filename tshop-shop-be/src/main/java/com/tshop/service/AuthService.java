package com.tshop.service;

import com.tshop.controller.dto.request.LoginRequest;
import com.tshop.controller.dto.request.UserRequest;
import com.tshop.controller.dto.response.DataResponse;

public interface AuthService {
    DataResponse register(UserRequest request);
    DataResponse login(LoginRequest request);
}
