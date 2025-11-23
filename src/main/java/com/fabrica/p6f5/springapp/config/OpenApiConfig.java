package com.fabrica.p6f5.springapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI Configuration for Swagger documentation.
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Billing and Payments API")
                .version("1.0.0")
                .description("REST API for managing invoices, shipments, and billing operations. " +
                           "This system simulates microservices architecture with separate modules " +
                           "for Invoice, Shipment, Audit, and PDF services.")
                .contact(new Contact()
                    .name("API Support")
                    .email("support@example.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Development Server"),
                new Server()
                    .url("https://api.example.com")
                    .description("Production Server")))
            .addSecurityItem(new SecurityRequirement()
                .addList("Bearer Authentication"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }
    
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("Enter your JWT token");
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}

