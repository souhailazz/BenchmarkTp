package com.projet.rest_benchmark.controller.spring;

import com.projet.rest_benchmark.dto.CategoryDto;
import com.projet.rest_benchmark.dto.ItemDto;
import com.projet.rest_benchmark.service.CategoryService;
import com.projet.rest_benchmark.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Profile("spring")
@RequestMapping("/api/spring/categories")
@RequiredArgsConstructor
public class SpringCategoryController {
    
    private final CategoryService categoryService;
    private final ItemService itemService;
    
    @GetMapping
    public ResponseEntity<Page<CategoryDto>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Page<CategoryDto> categories = categoryService.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        CategoryDto category = categoryService.findById(id);
        return ResponseEntity.ok(category);
    }
    
    @GetMapping("/{id}/items")
    public ResponseEntity<Page<ItemDto>> getCategoryItems(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Page<ItemDto> items = itemService.findByCategoryId(id, PageRequest.of(page, size));
        return ResponseEntity.ok(items);
    }
    
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto created = categoryService.create(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDto categoryDto) {
        
        CategoryDto updated = categoryService.update(id, categoryDto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

