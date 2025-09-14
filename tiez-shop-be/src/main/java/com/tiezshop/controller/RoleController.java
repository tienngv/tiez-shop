package com.tiezshop.controller;

import com.tiezshop.controller.dto.request.RoleDto;
import com.tiezshop.controller.dto.response.DataResponse;
import com.tiezshop.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RoleController {
    private final RoleService roleService;

    @Operation(summary = "Tạo mới", tags = "API - [ROLE]")
    @PostMapping("/role")
    public DataResponse<String> createRole(@RequestBody @Valid RoleDto request) {
        return DataResponse.<String>builder()
                .result(roleService.createRole(request)).build();
    }

    @Operation(summary = "Lấy toàn bộ", tags = "API - [ROLE]")
    @GetMapping("/roles")
    public DataResponse<List<RoleDto>> getAll() {
        return DataResponse.<List<RoleDto>>builder()
                .result(roleService.getAll()).build();
    }
}
