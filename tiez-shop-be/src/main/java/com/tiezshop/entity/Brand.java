package com.tiezshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "brands")
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(length = 500)
    private String description;

    @Size(max = 1024, message = "Logo URL must not exceed 1024 characters")
    @Column(length = 1024)
    private String logoUrl;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    @Column(length = 100)
    private String country;

    @Column(nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;
}
