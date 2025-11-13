package com.projet.rest_benchmark.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    
    @NotBlank
    @Size(max = 128)
    private String name;
    
    @NotNull
    @Positive
    private Double price;
    
    @NotNull
    private Integer stock;
    
    @NotNull
    private Long categoryId;
    
    private Instant updatedAt;
}

