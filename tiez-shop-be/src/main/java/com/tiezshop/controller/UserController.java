package com.tiezshop.controller;


import com.tiezshop.controller.dto.request.*;
import com.tiezshop.controller.dto.response.DataResponse;
import com.tiezshop.controller.dto.response.LoginResponse;
import com.tiezshop.controller.dto.response.UserResponse;

import com.tiezshop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Đăng ký", tags = "API - [USER]")
    @PostMapping("/register")
    public DataResponse<String> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return DataResponse.<String>builder()
                .result(userService.registerUser(registerRequest)).build();
    }

    @Operation(summary = "Đăng nhập", tags = "API - [USER]")
    @PostMapping("/login")
    public DataResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return DataResponse.<LoginResponse>builder()
                .result(userService.login(request)).build();
    }

    @Operation(summary = "Cập nhật", tags = "API - [USER]")
    @PutMapping("/{id}")
    public DataResponse<Void> updateUser(@PathVariable String id, @RequestBody UpdateUserRequest updateRequest) {
        userService.updateUser(id, updateRequest);
        return DataResponse.<Void>builder().build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Lấy thông tin chi tiết", tags = "API - [USER]")
    @GetMapping("/{id}")
    public DataResponse<UserResponse> getUserDetail(@PathVariable String id) {
        return DataResponse.<UserResponse>builder()
                .result(userService.getDetailUser(id)).build();
    }

    @Operation(summary = "Xóa", tags = "API - [USER]")
    @DeleteMapping("/{id}")
    public DataResponse<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return DataResponse.<Void>builder().build();
    }

    @Operation(summary = "Đổi mật khẩu", tags = "API - [USER]")
    @PostMapping("/change-pass")
    public DataResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return DataResponse.<Void>builder().build();
    }

    @Operation(summary = "Cập nhật Roles", tags = "API - [USER]")
    @PostMapping("/assign-roles")
    public DataResponse<Object> assignRoles(@RequestBody AssignRolesToUserRequest request) {
        userService.assignRolesToUser(request);
        return DataResponse.builder()
                .message(String.format("Assign roles user %s success", request.getUserId())).build();
    }

    @Operation(summary = "Danh sách - Paging", tags = "API - [USER]")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public DataResponse<Page<UserResponse>> getUsers(@RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) Long createdTimeFrom,
                                                     @RequestParam(required = false) Long createdTimeTo,
                                                     @RequestParam(required = false) List<String> roleIds,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size
    ) {
        Page<UserResponse> response = userService.getUsers(keyword, createdTimeFrom, createdTimeTo, roleIds, page, size);
        return DataResponse.<Page<UserResponse>>builder()
                .message("Search users success")
                .result(response)
                .build();
    }

}
