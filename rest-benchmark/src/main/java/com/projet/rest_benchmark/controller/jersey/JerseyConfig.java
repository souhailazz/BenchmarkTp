package com.projet.rest_benchmark.controller.jersey;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("jersey")
@ApplicationPath("/api/jersey")
public class JerseyConfig extends ResourceConfig {
    
    public JerseyConfig() {
        // Register JAX-RS resources
        register(JerseyCategoryResource.class);
        register(JerseyItemResource.class);
        
        // Register exception mappers
        register(EntityNotFoundExceptionMapper.class);
        register(ValidationExceptionMapper.class);
    }
}

