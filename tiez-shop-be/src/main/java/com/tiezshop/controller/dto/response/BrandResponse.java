package com.tiezshop.controller.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponse {
    
    private String id;
    private String name;
    private String description;
    private String logoUrl;
    private String country;
    private Boolean isActive;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
