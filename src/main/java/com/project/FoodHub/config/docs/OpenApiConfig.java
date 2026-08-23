package com.project.FoodHub.config.docs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI foodhubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FoodHub API")
                        .description("API para la gestión de recetas y creadores (nutricionitas) en FoodHub")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("FoodHub Support")
                        )
                );
    }
}
