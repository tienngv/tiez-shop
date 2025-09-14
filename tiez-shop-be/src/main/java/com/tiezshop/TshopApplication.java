package com.tiezshop;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Tài liệu đặc tả REST API",
                version = "1.0.0",
                description = "Tài liệu API của Tiez-Shop",
                contact = @Contact(
                        name = "Nguyen Gia Viet Tien",
                        url = "https://github.com/tienngv/tiez-shop"
                )
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@SpringBootApplication
public class TshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(TshopApplication.class, args);
    }

}
