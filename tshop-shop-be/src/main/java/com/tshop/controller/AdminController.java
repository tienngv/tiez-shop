package com.tshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AdminController {

    @GetMapping("/admin/dashboard")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("Welcome to admin dashboard");
    }

    @GetMapping("/staff")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getAdminDashboard1() {
        return ResponseEntity.ok("Welcome to dashboard");
    }
}
