package com.tiezshop.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import com.tiezshop.constrain.ErrorConst;
import com.tiezshop.exception.TiezShopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
@RequiredArgsConstructor
public class BaseRestTemplate {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

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

        try {
            ResponseEntity<T> response = restTemplate.exchange(url, method, entity, responseType);

            String requestLog = serializeSafely(requestBody);
            String responseLog = serializeSafely(response.getBody());
            log.info("[call url: {}] request: [{}] response: [{}]", url, requestLog, responseLog);

            return response;
        } catch (HttpStatusCodeException ex) {
            log.error("[err call url: {}], request [{}], response: [{}]",
                    url, serializeSafely(requestBody), ex.getResponseBodyAsString());
            throw new TiezShopException(String.valueOf(ex.getStatusCode().value()), ex.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("[err call url: {}] request [{}] exception: [{}]",
                    url, serializeSafely(requestBody), e.getMessage(), e);
            throw new TiezShopException(ErrorConst.UNKNOWN.toString(),
                    "Error connect to domain " + url + ": " + e.getMessage());
        }
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

