package com.tiezshop.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.tiezshop.configurations.security.KeycloakProperties;
import com.tiezshop.constrain.ErrorConst;
import com.tiezshop.controller.dto.identity.TokenExchangeResponse;
import com.tiezshop.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Log4j2
@Service
@RequiredArgsConstructor
public class BaseRestTemplate {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KeycloakProperties keycloakProperties;

    private static String accessToken;
    private static Instant expiresAt;

    public <T> ResponseEntity<T> callRestApi(
            String url,
            HttpMethod method,
            HttpHeaders headers,
            Object requestBody,
            Class<T> responseType
    ) {
        HttpEntity<Object> entity = (requestBody != null)
                ? new HttpEntity<>(requestBody, headers)
                : new HttpEntity<>(headers);

//        try {
        ResponseEntity<T> response = restTemplate.exchange(url, method, entity, responseType);

        String requestLog = serializeSafely(requestBody);
        String responseLog = serializeSafely(response.getBody());
        log.info("[call url: {}] request: [{}] response: [{}]", url, requestLog, responseLog);

        return response;
//        } catch (HttpStatusCodeException ex) {
//            log.error("[err call url: {}], request [{}], response: [{}]",
//                    url, serializeSafely(requestBody), ex.getResponseBodyAsString());
//            throw new AppException(String.valueOf(ex.getStatusCode().value()), ex.getResponseBodyAsString());
//        } catch (Exception e) {
//            log.error("[err call url: {}] request [{}] exception: [{}]",
//                    url, serializeSafely(requestBody), e.getMessage(), e);
//            throw new AppException(ErrorConst.UNKNOWN.toString(),
//                    "Error connect to domain " + url + ": " + e.getMessage());
//        }
    }

    public HttpHeaders getHeaders(String token) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setBearerAuth(token);
        return header;
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

        var response = callRestApi(
                keycloakProperties.getTokenUrl(),
                HttpMethod.POST,
                header,
                body,
                TokenExchangeResponse.class
        ).getBody();

        if (response != null && response.getAccessToken() != null) {
            accessToken = response.getAccessToken();
            long expiresIn = response.getExpiresIn() != null ? response.getExpiresIn() : 300;
            expiresAt = Instant.now().plusSeconds(expiresIn);

            return accessToken;
        }

        throw new AppException(ErrorConst.BAD_REQUEST.getErrCode(), "Cannot fetch access token from Keycloak");
    }

    private String serializeSafely(Object obj) {
        if (obj == null) return "";
        try {
            String json = objectMapper.writeValueAsString(obj);
            return json.length() > 1000 ? json.substring(0, 1000) + "...(truncated)" : json;
        } catch (Exception e) {
            return obj.toString();
        }
    }
}

