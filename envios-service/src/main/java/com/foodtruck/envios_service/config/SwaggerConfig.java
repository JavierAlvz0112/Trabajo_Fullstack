package com.foodtruck.envios_service.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("API FoodTruck - envios").version("1.0").description("Gestion de envios del sistema FoodTruck."));
    }
}
 