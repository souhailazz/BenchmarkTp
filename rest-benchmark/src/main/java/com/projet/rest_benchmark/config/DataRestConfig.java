package com.projet.rest_benchmark.config;

import com.projet.rest_benchmark.entity.Category;
import com.projet.rest_benchmark.entity.Item;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
@Profile("datarest")
public class DataRestConfig implements RepositoryRestConfigurer {
    
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        // Expose IDs in JSON responses
        config.exposeIdsFor(Category.class, Item.class);
        
        // Set base path
        config.setBasePath("/api/datarest");
        
        // Set default page size
        config.setDefaultPageSize(50);
        config.setMaxPageSize(200);
    }
}

