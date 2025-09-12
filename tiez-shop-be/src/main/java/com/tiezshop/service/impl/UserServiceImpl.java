package com.tiezshop.service.impl;

import com.google.gson.Gson;
import com.tiezshop.configurations.security.KeycloakProperties;
import com.tiezshop.constrain.ErrorConst;
import com.tiezshop.controller.dto.identity.Credential;
import com.tiezshop.controller.dto.identity.TokenExchangeResponse;
import com.tiezshop.controller.dto.identity.UserCreationRequest;
import com.tiezshop.controller.dto.request.AssignRolesToUserRequest;
import com.tiezshop.controller.dto.request.LoginRequest;
import com.tiezshop.controller.dto.request.RegisterRequest;
import com.tiezshop.controller.dto.request.RoleRequest;
import com.tiezshop.controller.dto.response.UserResponse;
import com.tiezshop.controller.mapper.UserMapper;
import com.tiezshop.entity.Role;
import com.tiezshop.entity.User;
import com.tiezshop.exception.AppException;
import com.tiezshop.repository.RoleRepository;
import com.tiezshop.repository.UserRepository;

import com.tiezshop.service.UserService;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final KeycloakProperties keycloakProperties;
    private final BaseRestTemplate baseRestTemplate;
    private final Gson gson;

    @Override
    public String registerUser(RegisterRequest registerRequest) {
        String token = baseRestTemplate.getToken();
        if (token.isEmpty()) {
            throw new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Token is empty");
        }

        UserCreationRequest userCreationRequest = UserCreationRequest.builder()
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .enabled(true)
                .emailVerified(false)
                .credentials(List.of(Credential.builder()
                        .type("password")
                        .value(registerRequest.getPassword())
                        .temporary(false)
                        .build()))
                .build();

        var responseEntity = baseRestTemplate.callRestApi(
                keycloakProperties.getRegistrationUrl(),
                HttpMethod.POST,
                baseRestTemplate.getHeaders(token),
                userCreationRequest,
                Object.class
        );

        String location = (responseEntity != null) ? responseEntity.getHeaders().getFirst("Location") : null;
        if (location == null) {
            throw new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Registration failed - no Location header");
        }
        String userId = location.substring(location.lastIndexOf("/") + 1);
        User user = userMapper.toEntity(registerRequest);
        user.setUserId(userId);
        userRepository.save(user);
        log.info("User '{}' created successfully in Keycloak with ID: {}", registerRequest.getUsername(), userId);

        return user.getUserId();
    }

    @Override
    public void assignRolesToUser(AssignRolesToUserRequest request) {
        String token = baseRestTemplate.getToken();
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "User not found"));
        List<RoleRequest> rolesToAssign = new ArrayList<>();
        if (request.getRoleIds() != null) {
            request.getRoleIds().forEach(roleId -> {
                Role role = roleRepository.findById(roleId).orElseThrow(() ->
                        new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Role with ID " + roleId + " not found"));
                user.getRoles().add(role);
                rolesToAssign.add(RoleRequest.builder().id(role.getId()).name(role.getName()).build());
            });
        }
        deleteRolesToUserKeyCloak(user.getUserId(), token);
        assignRolesToUserKeyCloak(user.getUserId(), rolesToAssign, token);
        log.info("Assign Roles To UserId : {}, Request :[{}]", user.getUserId(), gson.toJson(request));
        userRepository.saveAndFlush(user);
    }

    private void assignRolesToUserKeyCloak(String userId, List<RoleRequest> roles, String token) {
        ResponseEntity<Object> response = baseRestTemplate.callRestApi(
                keycloakProperties.getAssignRoleUrl().replace("{userId}", userId),
                HttpMethod.POST,
                baseRestTemplate.getHeaders(token),
                roles,
                Object.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Roles assignment failed");
        }
    }

    private void deleteRolesToUserKeyCloak(String userId, String token) {
        ResponseEntity<Object> response = baseRestTemplate.callRestApi(
                keycloakProperties.getAssignRoleUrl().replace("{userId}", userId),
                HttpMethod.DELETE,
                baseRestTemplate.getHeaders(token),
                null,
                Object.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Delete Roles in keycloak failed");
        }
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Override
    public UserResponse getDetailUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        var user = userRepository.findByUserId(userId).orElseThrow(
                () -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "User not found")
        );
        return userMapper.toDto(user);
    }

    @Override
    public UserResponse loginUser(LoginRequest loginRequest) {
        return null;
    }

}
