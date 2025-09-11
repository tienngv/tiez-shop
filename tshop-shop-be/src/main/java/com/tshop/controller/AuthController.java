package com.tshop.controller;

import com.tshop.controller.dto.request.LoginRequest;
import com.tshop.controller.dto.request.UserRequest;
import com.tshop.controller.dto.response.DataResponse;
import com.tshop.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/register")
    public DataResponse register(@RequestBody UserRequest request) {
        return authServiceImpl.register(request);
    }

    @PostMapping("/login")
    public DataResponse login(@RequestBody LoginRequest request) {
        return authServiceImpl.login(request);
    }
}


