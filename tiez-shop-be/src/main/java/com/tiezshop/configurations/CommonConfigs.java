package com.tiezshop.configurations;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


@Configuration
public class CommonConfigs {
    //i18n
    @Bean
    public LocaleResolver localeResolver() {
        var resolver = new AcceptHeaderLocaleResolver();
        var locales = Arrays.asList(Locale.ENGLISH, new Locale("vi"));
        resolver.setSupportedLocales(locales);
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .requestFactory(this::requestFactory)
                .build();
    }

    private HttpComponentsClientHttpRequestFactory requestFactory() {
        int connectTimeoutMillis = 5000;
        int responseTimeoutMillis = 60000;
        int connectionRequestTimeoutMillis = 3000;

        int defaultMaxPerRoute = 5;
        int maxTotal = 10;

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectTimeoutMillis))
                .setResponseTimeout(Timeout.ofMilliseconds(responseTimeoutMillis))
                .build();

        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(connectionRequestTimeoutMillis, TimeUnit.MILLISECONDS)
                .build();

        PoolingHttpClientConnectionManager connectionManager =
                PoolingHttpClientConnectionManagerBuilder.create()
                        .setDefaultSocketConfig(socketConfig)
                        .setMaxConnTotal(maxTotal)
                        .setMaxConnPerRoute(defaultMaxPerRoute)
                        .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictIdleConnections(TimeValue.ofSeconds(30))
                .evictExpiredConnections()
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

}
