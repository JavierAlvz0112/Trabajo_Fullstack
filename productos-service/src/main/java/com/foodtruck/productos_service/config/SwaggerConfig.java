package com.foodtruck.productos_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
@Configuration
public class SwaggerConfig {
 
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API FoodTruck - Productos")
                        .version("1.0")
                        .description("Gestión de productos del FoodTruck. " +
                                     "Incluye comidas (con tipo e ingredientes) y bebidas (con sabor, tamaño y sellos)."));
    }
}