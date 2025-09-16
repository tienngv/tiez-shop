package com.tiezshop.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandRequest {
    
    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Size(max = 1024, message = "Logo URL must not exceed 1024 characters")
    private String logoUrl;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    private Boolean isActive = true;
}
