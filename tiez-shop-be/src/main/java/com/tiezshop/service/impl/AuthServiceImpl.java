package com.tiezshop.service.impl;


import com.tiezshop.constrain.ErrorConst;
import com.tiezshop.controller.dto.request.LoginRequest;
import com.tiezshop.controller.dto.request.UserRequest;
import com.tiezshop.controller.dto.response.DataResponse;
import com.tiezshop.controller.mapper.UserMapper;
import com.tiezshop.entity.Role;
import com.tiezshop.entity.User;
import com.tiezshop.exception.TShopException;
import com.tiezshop.repository.RoleRepository;
import com.tiezshop.repository.UserRepository;
import com.tiezshop.configurations.security.JwtUtils;
import com.tiezshop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public DataResponse register(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new TShopException(ErrorConst.BAD_REQUEST.getErrCode(), "Username already exists");
        }
        Set<Role> roles = request.getRoles().stream()
                .map(role -> roleRepository.findByCode(role.getCode())
                        .orElseThrow(() -> new RuntimeException("Role not found: " + role.getCode())))
                .collect(Collectors.toSet());

        User user = userMapper.toEntity(request);
        user.setRoles(roles);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return new DataResponse(ErrorConst.SUCCESS);
    }

    public DataResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        Map<String, String> response = new HashMap<>();
        response.put("token", jwtUtils.generateToken(request.getUsername()));
        return new DataResponse(ErrorConst.SUCCESS, response);
    }


}
