package com.tiezshop.service;

import com.tiezshop.controller.dto.request.*;
import com.tiezshop.controller.dto.response.LoginResponse;
import com.tiezshop.controller.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    String registerUser(RegisterRequest registerRequest);

    LoginResponse login(LoginRequest loginRequest);

    void updateUser(String userId, UpdateUserRequest registerRequest);

    void deleteUser(String userId);

    Page<UserResponse> getUsers(String search, Long createdTimeFrom, Long createdTimeTo, List<String> roleIds, int page, int size);

    void assignRolesToUser(AssignRolesToUserRequest request);

    UserResponse getDetailUser(String userId);

    void changePassword(ChangePasswordRequest request);
}
