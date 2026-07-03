package com.foodtruck.almacen_service.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("API FoodTruck - almacen").version("1.0").description("Gestion de almacen del sistema FoodTruck."));
    }
}