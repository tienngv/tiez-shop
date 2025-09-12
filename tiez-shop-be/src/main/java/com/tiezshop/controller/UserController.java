package com.tiezshop.controller;


import com.tiezshop.controller.dto.request.AssignRolesToUserRequest;
import com.tiezshop.controller.dto.request.RegisterRequest;
import com.tiezshop.controller.dto.response.DataResponse;
import com.tiezshop.controller.dto.response.UserResponse;

import com.tiezshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public DataResponse<String> register(@RequestBody RegisterRequest registerRequest) {
        return DataResponse.<String>builder()
                .result(userService.registerUser(registerRequest))
                .build();
    }

    @GetMapping("/users")
    public DataResponse<List<UserResponse>> getAllUsers() {
        return DataResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/detail")
    public DataResponse<UserResponse> getUserDetail() {
        return DataResponse.<UserResponse>builder()
                .result(userService.getDetailUser())
                .build();
    }

    @PostMapping("/assign-roles")
    public DataResponse<Object> assignRoles(AssignRolesToUserRequest request) {
        userService.assignRolesToUser(request);
        return DataResponse.builder()
                .message(String.format("Assign roles user %s success", request.getUserId()))
                .build();
    }

//    @PostMapping("/login")
//    public DataResponse login(@RequestBody LoginRequest request) {
//        return userService.registerUser(registerRequest);
//    }
}
