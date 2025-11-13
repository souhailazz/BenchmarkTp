package com.projet.rest_benchmark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    
    @NotBlank
    @Size(max = 32)
    private String code;
    
    @NotBlank
    @Size(max = 128)
    private String name;
    
    private Instant updatedAt;
}

