package com.tiezshop.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {
    @NotBlank(message = "USERNAME IS MUST NOT BLANK")
    @Size(min = 4, message = "INVALID_USERNAME")
    private String username;
    @NotBlank(message = "PASSWORD IS MUST NOT BLANK")
    @Size(min = 6, message = "INVALID_PASSWORD")
    private String password;

    private String email;
    private String firstName;
    private String lastName;
    private String fullName;

    private String createdBy;
    @NotBlank(message = "PHONE NUMBER IS MUST NOT BLANK")
    private String phoneNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dob;

}
