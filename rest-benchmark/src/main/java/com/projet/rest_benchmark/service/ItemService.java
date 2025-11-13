package com.projet.rest_benchmark.service;

import com.projet.rest_benchmark.dto.ItemDto;
import com.projet.rest_benchmark.entity.Category;
import com.projet.rest_benchmark.entity.Item;
import com.projet.rest_benchmark.repository.CategoryRepository;
import com.projet.rest_benchmark.repository.ItemRepository;
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
public class ItemService {
    
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public Page<ItemDto> findAll(Pageable pageable) {
        return itemRepository.findAllItems(pageable)
                .map(this::toDto);
    }
    
    @Transactional(readOnly = true)
    public Page<ItemDto> findByCategoryId(Long categoryId, Pageable pageable) {
        return itemRepository.findAllByCategoryId(categoryId, pageable)
                .map(this::toDto);
    }
    
    @Transactional(readOnly = true)
    public ItemDto findById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
        return toDto(item);
    }
    
    public ItemDto create(@Valid ItemDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + dto.getCategoryId()));
        
        Item item = new Item();
        item.setName(dto.getName());
        item.setPrice(dto.getPrice());
        item.setStock(dto.getStock());
        item.setCategory(category);
        item.setUpdatedAt(Instant.now());
        
        Item saved = itemRepository.save(item);
        return toDto(saved);
    }
    
    public ItemDto update(Long id, @Valid ItemDto dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
        
        if (!item.getCategory().getId().equals(dto.getCategoryId())) {
            Category newCategory = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + dto.getCategoryId()));
            item.setCategory(newCategory);
        }
        
        item.setName(dto.getName());
        item.setPrice(dto.getPrice());
        item.setStock(dto.getStock());
        item.setUpdatedAt(Instant.now());
        
        Item updated = itemRepository.save(item);
        return toDto(updated);
    }
    
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new EntityNotFoundException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }
    
    private ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getStock(),
                item.getCategory().getId(),
                item.getUpdatedAt()
        );
    }
}

