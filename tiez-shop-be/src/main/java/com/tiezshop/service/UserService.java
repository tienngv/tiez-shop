package com.tiezshop.service;

import com.tiezshop.controller.dto.request.LoginRequest;
import com.tiezshop.controller.dto.request.RegisterRequest;
import com.tiezshop.controller.dto.response.DataResponse;

public interface UserService {
    DataResponse registerUser(RegisterRequest registerRequest);

    DataResponse loginUser(LoginRequest loginRequest);
}
