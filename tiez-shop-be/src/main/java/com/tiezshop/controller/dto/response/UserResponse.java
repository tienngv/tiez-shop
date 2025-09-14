package com.tiezshop.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tiezshop.controller.dto.request.RoleDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private UUID id;
    private String username;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private String createdBy;
    private String updatedBy;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String avatar;
    private String status;
    private List<RoleDto> roles;

}
