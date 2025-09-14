package com.tiezshop.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank(message = "USER_ID MUST NOT BE BLANK")
    private String userId;

    @NotBlank(message = "NEW PASSWORD MUST NOT BE BLANK")
    private String newPassword;
}
