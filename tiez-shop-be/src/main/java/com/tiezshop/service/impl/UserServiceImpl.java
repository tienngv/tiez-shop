package com.tiezshop.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.tiezshop.configurations.security.KeycloakProperties;
import com.tiezshop.configurations.specification.UserSpecification;
import com.tiezshop.constrain.ErrorConst;
import com.tiezshop.controller.dto.identity.Credential;
import com.tiezshop.controller.dto.identity.UserCreationRequest;
import com.tiezshop.controller.dto.request.*;
import com.tiezshop.controller.dto.response.LoginResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
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
    @Transactional
    public String registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new AppException(ErrorConst.CONFLICT.getErrCode(), "Tên người dùng '" + registerRequest.getUsername() + "' đã tồn tại");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new AppException(ErrorConst.CONFLICT.getErrCode(), "Email '" + registerRequest.getEmail() + "' đã được sử dụng");
        }
        String token = baseRestTemplate.getToken();

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

        ResponseEntity<Object> responseEntity;
        try {
            responseEntity = baseRestTemplate.callRestApi(
                    keycloakProperties.getRegistrationUrl(),
                    HttpMethod.POST,
                    baseRestTemplate.getHeaders(token),
                    userCreationRequest,
                    Object.class
            );
        } catch (HttpClientErrorException e) {
            log.error("Failed to create user in Keycloak: {}", e.getMessage());
            throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Tạo người dùng thất bại: " + e.getLocalizedMessage());
        }

        String location = responseEntity.getHeaders().getFirst("Location");
        if (location == null) {
            throw new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Đăng ký thất bại - không có header Location");
        }
        String userId = location.substring(location.lastIndexOf("/") + 1);

        User user = userMapper.toEntity(registerRequest);
        user.setId(userId);
        userRepository.save(user);
        log.info("User '{}' created successfully in Keycloak with ID: {}", registerRequest.getUsername(), userId);

        return userId;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "password");
            formData.add("client_id", keycloakProperties.getClientId());
            formData.add("client_secret", keycloakProperties.getClientSecret());
            formData.add("username", loginRequest.getUsername());
            formData.add("password", loginRequest.getPassword());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            ResponseEntity<JsonNode> response = baseRestTemplate.callRestApi(
                    keycloakProperties.getTokenUrl(),
                    HttpMethod.POST,
                    headers,
                    formData,
                    JsonNode.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Failed to authenticate with Keycloak for username: {}", loginRequest.getUsername());
                throw new AppException(ErrorConst.UNAUTHORIZED.getErrCode(), "Tên người dùng hoặc mật khẩu không đúng");
            }

            JsonNode tokenResponse = response.getBody();
            String accessToken = tokenResponse.get("access_token").asText();
            String refreshToken = tokenResponse.get("refresh_token").asText();
            long expiresIn = tokenResponse.get("expires_in").asLong();
            String tokenType = tokenResponse.get("token_type").asText();

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Không tìm thấy người dùng"));
            if (user.getStatus() == User.UserStatus.BLOCKED) {
                log.error("Login attempt for blocked user: {}", loginRequest.getUsername());
                throw new AppException(ErrorConst.UNAUTHORIZED.getErrCode(), "Tài khoản của bạn đã bị khóa");
            }

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(expiresIn)
                    .tokenType(tokenType)
                    .build();
        } catch (HttpClientErrorException e) {
            log.error("Keycloak authentication error: {}", e.getMessage());
            if (e.getStatusCode().value() == 401) {
                throw new AppException(ErrorConst.UNAUTHORIZED.getErrCode(), "Đăng nhập thất bại: Tên người dùng hoặc mật khẩu không đúng");
            }
            throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Đăng nhập thất bại: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during login: {}", e.getMessage());
            throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Đăng nhập thất bại: " + e.getMessage());
        }
    }


    @Override
    @Transactional
    public void updateUser(String userId, UpdateUserRequest request) {
        String token = baseRestTemplate.getToken();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Không tìm thấy người dùng"));
        userMapper.updateUser(user, request);

        UserCreationRequest updateRequest = UserCreationRequest.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .enabled(true)
                .build();
        try {
            baseRestTemplate.callRestApi(
                    keycloakProperties.getRegistrationUrl() + "/" + userId,
                    HttpMethod.PUT,
                    baseRestTemplate.getHeaders(token),
                    updateRequest,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Cập nhật người dùng trong Keycloak thất bại");

        }
        userRepository.save(user);
        log.info("User {} updated successfully", userId);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        String token = baseRestTemplate.getToken();
        ResponseEntity<Void> response = baseRestTemplate.callRestApi(
                keycloakProperties.getRegistrationUrl() + "/" + userId,
                HttpMethod.DELETE,
                baseRestTemplate.getHeaders(token),
                null,
                Void.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Xóa người dùng trong Keycloak thất bại");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Không tìm thấy người dùng"));
        user.setStatus(User.UserStatus.BLOCKED);
        userRepository.save(user);
        log.info("User {} deleted successfully", userId);
    }

    @Override
    @Transactional
    public void assignRolesToUser(AssignRolesToUserRequest request) {
        String token = baseRestTemplate.getToken();

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Không tìm thấy người dùng"));

        // Fetch current roles from Keycloak
        ResponseEntity<JsonNode> currentRolesResponse = baseRestTemplate.callRestApi(
                keycloakProperties.getAssignRoleUrl().replace("{userId}", request.getUserId()),
                HttpMethod.GET,
                baseRestTemplate.getHeaders(token),
                null,
                JsonNode.class
        );
        List<String> currentRoleNames = new ArrayList<>();
        if (currentRolesResponse.getStatusCode().is2xxSuccessful() && currentRolesResponse.getBody() != null) {
            currentRolesResponse.getBody().forEach(node -> currentRoleNames.add(node.get("name").asText()));
        }

        // Get new roles from request
        List<RoleDto> rolesToAssign = new ArrayList<>();
        if (request.getRoleIds() != null) {
            List<Role> roles = roleRepository.findAllById(request.getRoleIds());
            if (roles.size() != request.getRoleIds().size()) {
                throw new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Một hoặc nhiều vai trò không được tìm thấy");
            }
            rolesToAssign = roles.stream()
                    .map(role -> RoleDto.builder()
                            .id(role.getId())
                            .name(role.getName())
                            .build())
                    .collect(Collectors.toList());
        }

        // Remove roles that are no longer in the new list
        List<RoleDto> rolesToRemove = new ArrayList<>();
        for (String roleName : currentRoleNames) {
            if (!rolesToAssign.stream().anyMatch(r -> r.getName().equals(roleName))) {
                ResponseEntity<JsonNode> roleResponse = baseRestTemplate.callRestApi(
                        keycloakProperties.getRoleUrl() + "/" + roleName,
                        HttpMethod.GET,
                        baseRestTemplate.getHeaders(token),
                        null,
                        JsonNode.class
                );
                if (roleResponse.getStatusCode().is2xxSuccessful() && roleResponse.getBody() != null) {
                    rolesToRemove.add(RoleDto.builder()
                            .id(roleResponse.getBody().get("id").asText())
                            .name(roleName)
                            .build());
                }
            }
        }
        if (!rolesToRemove.isEmpty()) {
            deleteRolesToUserKeyCloak(request.getUserId(), rolesToRemove, token);
        }
        if (!rolesToAssign.isEmpty()) {
            assignRolesToUserKeyCloak(request.getUserId(), rolesToAssign, token);
        }

        // Update roles in database
        user.getRoles().clear();
        user.getRoles().addAll(roleRepository.findAllById(request.getRoleIds()));
        userRepository.save(user);
        log.info("Assigned roles to userId: {}, request: {}", user.getId(), gson.toJson(request));
    }

    private void assignRolesToUserKeyCloak(String userId, List<RoleDto> roles, String token) {
        try {
            baseRestTemplate.callRestApi(
                    keycloakProperties.getAssignRoleUrl().replace("{userId}", userId),
                    HttpMethod.POST,
                    baseRestTemplate.getHeaders(token),
                    roles,
                    Object.class
            );
        } catch (HttpClientErrorException e) {
            log.error("Failed to assign roles to user {}: {}", userId, e.getLocalizedMessage());
            throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Gán vai trò thất bại");
        }
    }

    private void deleteRolesToUserKeyCloak(String userId, List<RoleDto> roles, String token) {
        try {
            baseRestTemplate.callRestApi(
                    keycloakProperties.getAssignRoleUrl().replace("{userId}", userId),
                    HttpMethod.DELETE,
                    baseRestTemplate.getHeaders(token),
                    roles,
                    Object.class
            );
        } catch (HttpClientErrorException e) {
            log.error("Failed to delete roles for user {}: {}", userId, e.getLocalizedMessage());
            throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Xóa vai trò thất bại");
        }
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String token = baseRestTemplate.getToken();
        Credential credential = Credential.builder()
                .type("password")
                .value(request.getNewPassword())
                .temporary(false)
                .build();
        ResponseEntity<Void> response = baseRestTemplate.callRestApi(
                keycloakProperties.getRegistrationUrl() + "/" + request.getUserId() + "/reset-password",
                HttpMethod.PUT,
                baseRestTemplate.getHeaders(token),
                credential,
                Void.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Đổi mật khẩu thất bại");
        }
        log.info("Password changed for user {}", request.getUserId());
    }

    @Override
    public Page<UserResponse> getUsers(String keyword, Long createdTimeFrom, Long createdTimeTo, List<String> roleIds, int page, int size) {
        try {
            LocalDateTime from = null;
            LocalDateTime to = null;

            if (createdTimeFrom != null) {
                from = Instant.ofEpochMilli(createdTimeFrom)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }

            if (createdTimeTo != null) {
                to = Instant.ofEpochMilli(createdTimeTo)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }

            // Validate roleIds
            if (roleIds != null && !roleIds.isEmpty()) {
                List<Role> roles = roleRepository.findAllById(roleIds);
                if (roles.size() != roleIds.size()) {
                    throw new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Một hoặc nhiều ID vai trò không được tìm thấy");
                }
            } else {
                roleIds = null;
            }

            PageRequest pageable = PageRequest.of(page, size);
            Page<User> userPage = userRepository.findAll(
                    UserSpecification.filter(keyword, from, to, roleIds),
                    pageable
            );

            return userPage.map(userMapper::toDto);

        } catch (Exception e) {
            log.error("Failed to search users: {}", e.getMessage(), e);
            throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Tìm kiếm người dùng thất bại: " + e.getMessage());
        }
    }

    @Override
    public UserResponse getDetailUser(String userId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Không tìm thấy người dùng")
        );
        return userMapper.toDto(user);
    }


}