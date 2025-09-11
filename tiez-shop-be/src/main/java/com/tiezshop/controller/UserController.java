package com.tiezshop.controller;

import com.tiezshop.controller.dto.request.LoginRequest;
import com.tiezshop.controller.dto.request.RegisterRequest;
import com.tiezshop.controller.dto.response.DataResponse;
import com.tiezshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public DataResponse register(@RequestBody RegisterRequest registerRequest) {
        return userService.registerUser(registerRequest);
    }

//    @PostMapping("/login")
//    public DataResponse login(@RequestBody LoginRequest request) {
//        return userService.registerUser(registerRequest);
//    }
}
