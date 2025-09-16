package com.tiezshop.controller.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean isActive;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
