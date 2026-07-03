package com.foodtruck.usuarios_service.config;

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
                        .title("API FoodTruck - Usuarios")
                        .version("1.0")
                        .description("Gestión de usuarios del sistema FoodTruck. " +
                                     "Permite crear, consultar, actualizar y eliminar usuarios, " +
                                     "además de consultar sus pedidos y total de pagos."));
    }
}
