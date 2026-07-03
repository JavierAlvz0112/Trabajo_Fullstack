package com.foodtruck.pedidos_service.config;

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
                        .title("API FoodTruck - Pedidos")
                        .version("1.0")
                        .description("Gestión de pedidos del sistema FoodTruck. Permite crear y gestionar pedidos con sus detalles."));
    }
}
