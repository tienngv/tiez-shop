package com.tiezshop.controller.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String avatar;
    private String status;
}
