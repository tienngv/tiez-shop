package com.tiezshop.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "USERNAME CAN'T EMPTY")
    private String username;
    @NotBlank(message = "PASSWORD CAN'T EMPTY")
    private String password;
}