package com.tiezshop.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.tiezshop.configurations.security.KeycloakProperties;
import com.tiezshop.constrain.ErrorConst;
import com.tiezshop.controller.dto.request.RoleDto;
import com.tiezshop.controller.mapper.RoleMapper;
import com.tiezshop.entity.Role;
import com.tiezshop.exception.AppException;
import com.tiezshop.repository.RoleRepository;
import com.tiezshop.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final BaseRestTemplate baseRestTemplate;
    private final KeycloakProperties keycloakProperties;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public String createRole(RoleDto request) {
        String token = baseRestTemplate.getToken();
        String roleUrl = keycloakProperties.getRoleUrl();
        try {
            ResponseEntity<JsonNode> checkResponse = baseRestTemplate.callRestApi(
                    roleUrl + "/" + request.getName(),
                    HttpMethod.GET,
                    baseRestTemplate.getHeaders(token),
                    null,
                    JsonNode.class
            );
            if (checkResponse.getStatusCode().is2xxSuccessful()) {
                throw new AppException(ErrorConst.CONFLICT.getErrCode(), "Role '" + request.getName() + "' already exists");
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() != 404) {
                log.error("Error checking role existence: {}", e.getMessage());
                throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Failed to check role existence");
            }
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("name", request.getName());
        payload.put("description", request.getDescription());
        payload.put("composite", false);
        payload.put("clientRole", false);

        ResponseEntity<Void> createResponse;
        try {
            createResponse = baseRestTemplate.callRestApi(
                    roleUrl,
                    HttpMethod.POST,
                    baseRestTemplate.getHeaders(token),
                    payload,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            log.error("Failed to create role in Keycloak: {}", e.getMessage());
            throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Failed to create role: " + e.getMessage());
        }

        if (!createResponse.getStatusCode().is2xxSuccessful()) {
            throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Failed to create role in Keycloak");
        }

        String roleId = extractRoleIdFromLocation(createResponse);
        if (roleId == null) {
            ResponseEntity<JsonNode> roleInfo = baseRestTemplate.callRestApi(
                    roleUrl + "/" + request.getName(),
                    HttpMethod.GET,
                    baseRestTemplate.getHeaders(token),
                    null,
                    JsonNode.class
            );
            JsonNode roleData = roleInfo.getBody();
            if (roleData == null || roleData.get("id") == null) {
                log.error("Unable to retrieve role information from Keycloak");
                throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Unable to retrieve role information");
            }
            roleId = roleData.get("id").asText();
        }

        Role role = roleMapper.toEntity(request);
        role.setId(roleId);
        roleRepository.save(role);
        log.info("Role '{}' created successfully in Keycloak with ID: {}", request.getName(), roleId);

        return roleId;
    }

    @Override
    public List<RoleDto> getAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .toList();
    }

    private String extractRoleIdFromLocation(ResponseEntity<Void> response) {
        String location = response.getHeaders().getFirst("Location");
        if (location != null) {
            String lastInfo = location.substring(location.lastIndexOf("/") + 1);
            try {
                UUID.fromString(lastInfo);
                return lastInfo;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
