package com.tiezshop.service.impl;

import com.tiezshop.configurations.security.KeycloakProperties;
import com.tiezshop.constrain.ErrorConst;
import com.tiezshop.controller.dto.identity.Credential;
import com.tiezshop.controller.dto.identity.TokenExchangeResponse;
import com.tiezshop.controller.dto.identity.UserCreationRequest;
import com.tiezshop.controller.dto.request.LoginRequest;
import com.tiezshop.controller.dto.request.RegisterRequest;
import com.tiezshop.controller.dto.response.DataResponse;
import com.tiezshop.controller.mapper.UserMapper;
import com.tiezshop.entity.User;
import com.tiezshop.exception.TiezShopException;
import com.tiezshop.repository.UserRepository;

import com.tiezshop.service.UserService;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakProperties keycloakProperties;
    private final BaseRestTemplate baseRestTemplate;
    private String accessToken;
    private Instant expiresAt;

    @Override
    public DataResponse registerUser(RegisterRequest registerRequest) {
        String token = getToken();
        if (token.isEmpty()) {
            throw new TiezShopException(ErrorConst.BAD_REQUEST.getErrCode(), "Token is empty");
        }

        UserCreationRequest userCreationRequest = UserCreationRequest.builder()
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .dob(registerRequest.getDob())
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
                getHeaders(token),
                userCreationRequest,
                Object.class
        );

        String location = (responseEntity != null) ? responseEntity.getHeaders().getFirst("Location") : null;
        if (location == null) {
            throw new TiezShopException(ErrorConst.BAD_REQUEST.getErrCode(), "Registration failed - no Location header");
        }
        String userId = location.substring(location.lastIndexOf("/") + 1);
        User user = userMapper.toEntity(registerRequest);
        user.setUserId(userId);
        userRepository.save(user);

        return new DataResponse(ErrorConst.SUCCESS, Map.of("userId", userId));
    }

    @Override
    public DataResponse loginUser(LoginRequest loginRequest) {
        return null;
    }

    public synchronized String getToken() {
        if (accessToken != null && expiresAt != null && Instant.now().isBefore(expiresAt.minusSeconds(30))) {
            return accessToken;
        }
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", "openid");
        body.add("client_id", keycloakProperties.getClientId());
        body.add("client_secret", keycloakProperties.getClientSecret());

        var response = baseRestTemplate.callRestApi(
                keycloakProperties.getTokenUrl(),
                HttpMethod.POST,
                header,
                body,
                TokenExchangeResponse.class
        ).getBody();

        if (response != null && response.getAccessToken() != null) {
            this.accessToken = response.getAccessToken();
            long expiresIn = response.getExpiresIn() != null ? response.getExpiresIn() : 300;
            this.expiresAt = Instant.now().plusSeconds(expiresIn);

            return accessToken;
        }

        throw new TiezShopException(ErrorConst.BAD_REQUEST.getErrCode(), "Cannot fetch access token from Keycloak");
    }


    private HttpHeaders getHeaders(String token) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setBearerAuth(token);
        return header;
    }
}
