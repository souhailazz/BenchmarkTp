package com.projet.rest_benchmark.controller.spring;

import com.projet.rest_benchmark.dto.ItemDto;
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
@RequestMapping("/api/spring/items")
@RequiredArgsConstructor
public class SpringItemController {
    
    private final ItemService itemService;
    
    @GetMapping
    public ResponseEntity<Page<ItemDto>> getItems(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Page<ItemDto> items;
        if (categoryId != null) {
            items = itemService.findByCategoryId(categoryId, PageRequest.of(page, size));
        } else {
            items = itemService.findAll(PageRequest.of(page, size));
        }
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id) {
        ItemDto item = itemService.findById(id);
        return ResponseEntity.ok(item);
    }
    
    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto) {
        ItemDto created = itemService.create(itemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemDto itemDto) {
        
        ItemDto updated = itemService.update(id, itemDto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

