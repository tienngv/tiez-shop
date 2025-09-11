package com.tiezshop.controller;


import com.tiezshop.controller.dto.request.RegisterRequest;
import com.tiezshop.controller.dto.response.DataResponse;
import com.tiezshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public DataResponse register(@RequestBody RegisterRequest registerRequest) {
        return userService.registerUser(registerRequest);
    }

    @GetMapping("/users")
    public DataResponse getAllUsers() {
        return userService.getAllUsers();
    }

//    @PostMapping("/login")
//    public DataResponse login(@RequestBody LoginRequest request) {
//        return userService.registerUser(registerRequest);
//    }
}
