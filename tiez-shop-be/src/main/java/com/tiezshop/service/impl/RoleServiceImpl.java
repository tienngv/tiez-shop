package com.tiezshop.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.tiezshop.configurations.security.KeycloakProperties;
import com.tiezshop.constrain.ErrorConst;
import com.tiezshop.controller.dto.request.RoleRequest;
import com.tiezshop.entity.Role;
import com.tiezshop.exception.AppException;
import com.tiezshop.repository.RoleRepository;
import com.tiezshop.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final BaseRestTemplate baseRestTemplate;
    private final KeycloakProperties keycloakProperties;

    public String createRole(RoleRequest request) {
        String token = baseRestTemplate.getToken();
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", request.getName());
        payload.put("description", request.getDescription());
        payload.put("composite", false);
        payload.put("clientRole", false);

        ResponseEntity<Void> response = baseRestTemplate.callRestApi(
                keycloakProperties.getRoleUrl(),
                HttpMethod.POST,
                baseRestTemplate.getHeaders(token),
                payload,
                Void.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Failed to create role in Keycloak");
        }
        ResponseEntity<JsonNode> roleInfo = baseRestTemplate.callRestApi(
                keycloakProperties.getRoleUrl() + "/" + request.getName(),
                HttpMethod.GET,
                baseRestTemplate.getHeaders(token),
                null,
                JsonNode.class
        );
        var roleData = roleInfo.getBody();
        if (roleData == null || roleData.get("id") == null) {
            throw new AppException(ErrorConst.UNKNOWN.getErrCode(), "Unable to retrieve role information from Keycloak");
        }
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setRoleId(roleData.get("id").asText());
        roleRepository.save(role);
        log.info("Role '{}' created successfully in Keycloak with ID: {}", request.getName(), role.getRoleId());

        return role.getRoleId();
    }
}
