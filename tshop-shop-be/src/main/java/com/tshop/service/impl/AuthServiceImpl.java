package com.tshop.service.impl;


import com.tshop.constrain.ErrorConst;
import com.tshop.controller.dto.request.LoginRequest;
import com.tshop.controller.dto.request.UserRequest;
import com.tshop.controller.dto.response.DataResponse;
import com.tshop.controller.mapper.UserMapper;
import com.tshop.entity.Role;
import com.tshop.entity.User;
import com.tshop.exception.TShopException;
import com.tshop.repository.RoleRepository;
import com.tshop.repository.UserRepository;
import com.tshop.configurations.security.JwtUtils;
import com.tshop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
