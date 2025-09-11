package com.tiezshop.controller.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class UserRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String avatar;
    private Set<RoleRequest> roles;
}
