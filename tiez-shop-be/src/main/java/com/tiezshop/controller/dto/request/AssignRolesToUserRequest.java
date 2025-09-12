package com.tiezshop.controller.dto.request;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRolesToUserRequest {
    private UUID userId;
    private List<UUID> roleIds;
}
