package com.tiezshop.configurations.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    private String clientId;
    private String clientSecret;
    private String tokenUrl;
    private String roleUrl;
    private String registrationUrl;
    private String realm;
    private String assignRoleUrl;
}
