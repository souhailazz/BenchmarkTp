package com.projet.rest_benchmark.service;

import com.projet.rest_benchmark.dto.CategoryDto;
import com.projet.rest_benchmark.entity.Category;
import com.projet.rest_benchmark.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public Page<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAllCategories(pageable)
                .map(this::toDto);
    }
    
    @Transactional(readOnly = true)
    public CategoryDto findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        return toDto(category);
    }
    
    public CategoryDto create(@Valid CategoryDto dto) {
        Category category = new Category();
        category.setCode(dto.getCode());
        category.setName(dto.getName());
        category.setUpdatedAt(Instant.now());
        
        Category saved = categoryRepository.save(category);
        return toDto(saved);
    }
    
    public CategoryDto update(Long id, @Valid CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        
        category.setCode(dto.getCode());
        category.setName(dto.getName());
        category.setUpdatedAt(Instant.now());
        
        Category updated = categoryRepository.save(category);
        return toDto(updated);
    }
    
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
    
    private CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getUpdatedAt()
        );
    }
}

