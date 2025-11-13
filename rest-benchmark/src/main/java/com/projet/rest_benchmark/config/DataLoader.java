package com.projet.rest_benchmark.config;

import com.projet.rest_benchmark.entity.Category;
import com.projet.rest_benchmark.entity.Item;
import com.projet.rest_benchmark.repository.CategoryRepository;
import com.projet.rest_benchmark.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Data loader to populate database with test data
 * Only runs if profile 'load-data' is active
 * 
 * Usage: java -jar app.jar --spring.profiles.active=spring,load-data
 */
@Configuration
@Profile("load-data")
@Slf4j
@RequiredArgsConstructor
public class DataLoader {
    
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    
    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            log.info("Starting data loading...");
            
            // Check if data already exists
            if (categoryRepository.count() > 0) {
                log.warn("Data already exists. Skipping data loading.");
                return;
            }
            
            // Generate categories
            log.info("Creating 2000 categories...");
            List<Category> categories = new ArrayList<>();
            for (int i = 1; i <= 2000; i++) {
                Category category = new Category();
                category.setCode(String.format("CAT%04d", i));
                category.setName(String.format("Category %04d", i));
                category.setUpdatedAt(Instant.now());
                categories.add(category);
                
                if (i % 500 == 0) {
                    categoryRepository.saveAll(categories);
                    categories.clear();
                    log.info("Created {} categories", i);
                }
            }
            if (!categories.isEmpty()) {
                categoryRepository.saveAll(categories);
            }
            
            long categoryCount = categoryRepository.count();
            log.info("✓ Created {} categories", categoryCount);
            
            // Generate items
            log.info("Creating ~100,000 items (50 per category)...");
            List<Category> allCategories = categoryRepository.findAll();
            Random random = new Random();
            
            String[] itemNames = {
                "Laptop", "Mouse", "Keyboard", "Monitor", "Headset", "Webcam", "Speaker",
                "Tablet", "Smartphone", "Charger", "Cable", "Adapter", "Router", "Switch",
                "Printer", "Scanner", "Microphone", "Camera", "Projector", "Hard Drive",
                "SSD", "RAM", "CPU", "GPU", "Motherboard", "Power Supply", "Case", "Fan"
            };
            
            List<Item> items = new ArrayList<>();
            int itemCount = 0;
            
            for (Category category : allCategories) {
                // Random number of items per category (40-60, avg 50)
                int numItems = 40 + random.nextInt(21);
                
                for (int j = 0; j < numItems; j++) {
                    Item item = new Item();
                    item.setName(itemNames[random.nextInt(itemNames.length)] + " " + String.format("%03d", random.nextInt(1000)));
                    item.setPrice(Math.round((9.99 + random.nextDouble() * 990.0) * 100.0) / 100.0);
                    item.setStock(random.nextInt(501));
                    item.setCategory(category);
                    item.setUpdatedAt(Instant.now());
                    items.add(item);
                    itemCount++;
                    
                    if (items.size() >= 1000) {
                        itemRepository.saveAll(items);
                        items.clear();
                        log.info("Created {} items...", itemCount);
                    }
                }
            }
            
            if (!items.isEmpty()) {
                itemRepository.saveAll(items);
            }
            
            long finalItemCount = itemRepository.count();
            log.info("✓ Created {} items", finalItemCount);
            log.info("Data loading completed successfully!");
        };
    }
}

