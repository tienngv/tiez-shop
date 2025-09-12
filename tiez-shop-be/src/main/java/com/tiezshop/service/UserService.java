package com.tiezshop.service;

import com.tiezshop.controller.dto.request.AssignRolesToUserRequest;
import com.tiezshop.controller.dto.request.LoginRequest;
import com.tiezshop.controller.dto.request.RegisterRequest;
import com.tiezshop.controller.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    String registerUser(RegisterRequest registerRequest);
    List<UserResponse> getAllUsers();
    void assignRolesToUser(AssignRolesToUserRequest request);
    UserResponse getDetailUser();
    UserResponse loginUser(LoginRequest loginRequest);
}
